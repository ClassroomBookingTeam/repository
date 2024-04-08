(ns server.db
  (:require [clojure.java.jdbc]
            [honeysql.core :as sql]
            [honeysql.format :as sql.format]
            [honeysql.types :as sql.types]
            [jsonista.core :as json]
            [next.jdbc :as jdbc]
            [next.jdbc.date-time :as jdbc.date-time]
            [next.jdbc.prepare :as jdbc.prepare]
            [next.jdbc.result-set :as next.jdbc.result-set]
            [server.const :as const])
  (:refer-clojure :exclude [format])
  (:import (clojure.lang
            IPersistentList
            IPersistentMap
            IPersistentSet
            IPersistentVector)
           (com.impossibl.postgres.api.data
            InetAddr
            Interval)
           (java.sql
            Date
            PreparedStatement
            ResultSet
            ResultSetMetaData
            Timestamp)
           (java.time
            ZoneId
            ZonedDateTime)
           (java.time.temporal
            ChronoUnit)
           (org.postgresql.util
            PGobject)))

(next.jdbc.date-time/read-as-instant)

(defn- default-column-reader
  [^ResultSet rs ^ResultSetMetaData rsm ^Integer i]
  (when-let [x (.getObject rs i)]
    (case (.getColumnTypeName rsm i)
      ("jsonb" "json") (json/read-value x)
      x)))

(def ^:private default-builder-fn
  next.jdbc.result-set/as-kebab-maps)

(def ^:private default-opts
  {:builder-fn (next.jdbc.result-set/as-maps-adapter default-builder-fn default-column-reader)})

(defn execute!
  "Executes `query`, returns multiple data."
  ([ds query]
   (execute! ds query default-opts))

  ([ds query opts]
   (jdbc/execute! ds query (merge default-opts opts))))

(defn execute-one!
  "Executes `query`, returns only first row"
  ([ds query]
   (execute-one! ds query default-opts))

  ([ds query opts]
   (jdbc/execute-one! ds query (merge default-opts opts))))

;; SQL

(defn format
  [query]
  (sql/format query))

(defn as-value
  [x]
  (sql.format/value x))

(defn as-raw
  [x]
  (sql.types/raw x))

(defn as-call
  [& x]
  (apply sql.types/call x))

(defn as-json
  [data]
  (some-> data
          (with-meta {:pg-type "json"})
          as-value))

(defn as-jsonb
  [data]
  (some-> data
          (with-meta {:pg-type "jsonb"})
          as-value))

(defn- decode-interval
  [^Interval v]
  (when v
    (let [seconds (.get v ChronoUnit/SECONDS)
          days-in-seconds (* 24 60 60 (.get v ChronoUnit/DAYS))
          nanoseconds (.get v ChronoUnit/NANOS)]
      (if (and (pos? nanoseconds)
               (neg? seconds))
        (+ days-in-seconds (inc seconds))
        (+ days-in-seconds seconds)))))

(defn- encode-pg-object
  [v]
  (let [pg-type (or (:pg-type (meta v)) "jsonb")
        value (json/write-value-as-string v)]
    (doto (PGobject.)
      (.setType pg-type)
      (.setValue value))))

(defn- decode-pg-object
  [^PGobject v]
  (let [pg-type (.getType v)
        value (.getValue v)]
    (if (contains? #{"json" "jsonb"} pg-type)
      (with-meta (json/read-value value) {:pg-type pg-type})
      value)))

;; HoneySQL extension

(extend-protocol sql.format/Parameterizable
  IPersistentVector
  (to-params [value pname]
    (sql.format/to-params-default value pname))

  IPersistentList
  (to-params [value pname]
    (sql.format/to-params-default value pname))

  IPersistentSet
  (to-params [value pname]
    (sql.format/to-params-default value pname)))

;; next.jdbc protocols

(extend-protocol jdbc.prepare/SettableParameter
  ZonedDateTime
  (set-parameter [^ZonedDateTime t ^PreparedStatement s ^long i]
    (.setObject s i (some-> t
                            (.withZoneSameInstant (ZoneId/of const/UTC_TIMEZONE_ID))
                            (.toInstant)
                            (Timestamp/from))))

  IPersistentMap
  (set-parameter [v ^PreparedStatement s ^long i]
    (.setObject s i (encode-pg-object v)))

  IPersistentVector
  (set-parameter [v ^PreparedStatement s ^long i]
                 (.setObject s i (encode-pg-object v))))

;; decoders

(extend-protocol next.jdbc.result-set/ReadableColumn
  InetAddr
  (read-column-by-label [^InetAddr v _]
                        (str v))
  (read-column-by-index [^InetAddr v _ _]
                        (str v))

  Date
  (read-column-by-label [^Date v _]
                        (.toLocalDate v))
  (read-column-by-index [^Date v _ _]
                        (.toLocalDate v))

  Interval
  (read-column-by-label [^Interval v _]
                        (decode-interval v))
  (read-column-by-index [^Interval v _ _]
                        (decode-interval v))

  PGobject
  (read-column-by-label [^PGobject v _]
                        (decode-pg-object v))
  (read-column-by-index [^PGobject v _ _]
                        (decode-pg-object v)))
