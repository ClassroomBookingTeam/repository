(ns server.time
  (:require [clojure.spec.alpha :as s]
            [server.util :as u])
  (:refer-clojure :exclude [format])
  (:import [java.time ZonedDateTime ZoneId LocalDateTime]
           java.util.Date))

(set! *warn-on-reflection* true)

(s/def ::date (partial instance? Date))
(s/def ::local-date-time (partial instance? LocalDateTime))

(def ^Date MIN_DATE
  "Smallest possible date in Java."
  (Date. Long/MIN_VALUE))

(def ^Date MAX_DATE
  "The greatest possible date in Java."
  (Date. Long/MAX_VALUE))

(s/fdef now
  :ret ::date)

(defn now
  "Constructs a Java Date object that represents the current time,
   measured to the nearest millisecond."
  ^Date []
  (Date.))

(s/fdef str->inst
  :args (s/cat :date-string string?)
  :ret inst?)

(defn str->inst
  "Converts `date-string` (datetime string in ISO 8601 format) to inst."
  [date-string]
  (-> date-string
      ZonedDateTime/parse
      .toInstant
      Date/from))

(s/fdef inst->local-date-time
  :args (s/cat :date inst?)
  :ret ::local-date-time)

(defn inst->local-date-time
  [^Date date]
  (-> date
      .toInstant
      (.atZone (ZoneId/of "UTC"))
      .toLocalDateTime))

(s/fdef local-date-time->inst
  :args (s/cat :date ::local-date-time)
  :ret inst?)

(defn local-date-time->inst
  [^LocalDateTime date]
  (-> date
      (.atZone (ZoneId/of "UTC"))
      .toInstant
      Date/from))

(s/fdef after?
  :args (s/cat :lhs (s/or ::date inst?)
               :rhs (s/or ::date inst?))
  :ret boolean?)

(defn after?
  [lhs rhs]
  (= (.compareTo lhs rhs) 1))

(s/fdef equal?
  :args (s/cat :lhs (s/or ::date inst?)
               :rhs (s/or ::date inst?))
  :ret boolean?)

(defn equal?
  [lhs rhs]
  (= (.compareTo lhs rhs) 0))

(s/fdef plus-days
  :args (s/cat :date ::local-date-time
               :days pos-int?)
  :ret ::local-date-time)

(defn plus-days
  [^LocalDateTime date days]
  (.plusDays date days))

(s/fdef plus-months
  :args (s/cat :date ::local-date-time
               :months pos-int?)
  :ret ::local-date-time)

(defn plus-months
  [^LocalDateTime date months]
  (.plusMonths date months))

(u/instrument-ns *ns*)
