(ns server.runtime
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [superstring.core :as string])
  (:import (com.zaxxer.hikari
            HikariDataSource)))

(s/def ::cfg map?)
(s/def ::pg-ds (s/spec #(instance? HikariDataSource %)
                       :gen #(gen/return (constantly :mock-ds))))
(s/def ::auth-user-id uuid?)

(s/def ::*ctx* (s/keys :req [::cfg
                             ::pg-ds
                             ::auth-user-id]))

(s/def *ctx* ::*ctx*)

(def ^{:dynamic true}
  *ctx*
  nil)

(defonce ^{:doc "A map of environment variables."}
  env
  (->> (merge {} (System/getenv) (System/getProperties))
       (map (fn [[str v]]
              (let [k (-> (string/lower-case str)
                          (string/replace "_" "-")
                          (string/replace "." "-")
                          (keyword))]
                [k v])))
       (into {})))

(declare ^{:doc "Holds cfg loaded from spica-cfg.edn"}
 cfg)

(defn bind-cfg
  "Binds `x` to `server.runtime/cfg` symbol."
  [x]
  (def cfg
    x))

(declare ^javax.sql.DataSource pg-ds)

(defn bind-pg-ds
  [pg-ds]
  (def pg-ds pg-ds))

(declare ^{:doc "Holds nrepl server if it was started."}
 nrepl-server)

(defn bind-nrepl-server
  [x]
  (def nrepl-server
    x))

(declare ^{:doc "Holds jetty server if it was started."}
 jetty-server)

(defn bind-jetty-server
  [x]
  (def jetty-server
    x))

(def starting-up?
  "Holds a flag indicating wether system started or not."
  (atom nil))

(def shutting-down?
  "Holds a flag indicating wether shutdown process started or not."
  (atom false))
