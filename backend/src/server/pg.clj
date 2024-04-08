(ns server.pg
  (:require [hikari-cp.core :as hikari-cp])
  (:import
   (com.zaxxer.hikari
    HikariDataSource)))

(defn- make-ds-opts
  [{:keys [connection-timeout
           auto-commit
           validation-timeout
           idle-timeout
           max-lifetime
           minimum-idle
           maximum-pool-size
           pool-name
           adapter
           username
           password
           database-name
           server-name
           port-number
           register-mbeans
           jdbc-url
           leak-detection-threshold]}]
  {:connection-timeout connection-timeout
   :auto-commit auto-commit
   :validation-timeout validation-timeout
   :idle-timeout idle-timeout
   :max-lifetime max-lifetime
   :minimum-idle minimum-idle
   :maximum-pool-size maximum-pool-size
   :pool-name pool-name
   :adapter adapter
   :username username
   :password password
   :database-name database-name
   :server-name server-name
   :port-number (int port-number)
   :register-mbeans register-mbeans
   :jdbc-url jdbc-url
   :leak-detection-threshold leak-detection-threshold
   :connection-init-sql "SET TIME ZONE 'UTC';"
   :read-only false})

(defn connect!
  [cfg]
  (let [ds-opts (make-ds-opts (:main-db cfg))]
    (hikari-cp/make-datasource ds-opts)))

(defn disconnect!
  [^HikariDataSource ds]
  (hikari-cp.core/close-datasource ds))
