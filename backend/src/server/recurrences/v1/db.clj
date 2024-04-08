(ns server.recurrences.v1.db
  (:require [server.db :as db])
  (:import (com.zaxxer.hikari
            HikariDataSource)))

(defn list-recurrences
  [^HikariDataSource ds]
  (->> {:select [:rec.id
                 :rec.name]
        :from [[:classroombooking.recurrence :rec]]}
       db/format
       (db/execute! ds)))
