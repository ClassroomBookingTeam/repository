(ns server.buildings.v1.db
  (:require [server.db :as db])
  (:import (com.zaxxer.hikari
            HikariDataSource)))

(defn list-buildings
  [^HikariDataSource ds]
  (->> {:select [:*]
        :from [:classroombooking.building]}
       db/format
       (db/execute! ds)))
