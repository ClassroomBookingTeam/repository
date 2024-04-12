(ns server.migrations
  (:require [migratus.core :as migratus]))

(defn- make-config
  [{:keys [server-name
           port-number
           database-name
           username
           password
           migration-dir
           migrations-table]}]
  {:store :database
   :migration-dir migration-dir
   :init-script "init.sql"
   :migrations-table migrations-table
   :init-in-transaction? false
   :db {:subprotocol "postgresql"
        :subname (str "//" server-name ":" port-number "/" database-name)
        :user username
        :password password}})

(defn migrate-db
  [cfg]
  (let [migratus-config (-> cfg
                            :main-db
                            make-config)]
    (migratus/init migratus-config)
    (migratus/migrate migratus-config)))
