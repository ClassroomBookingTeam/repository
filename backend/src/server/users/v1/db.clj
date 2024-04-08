(ns server.users.v1.db
  (:require [server.db :as db])
  (:import (com.zaxxer.hikari
            HikariDataSource)))

(defn get-by-email
  [^HikariDataSource ds email]
  (when email
    (->> {:select [:u.*]
          :from [[:classroombooking.user :u]]
          :where [:= :u.email email]}
         db/format
         (db/execute-one! ds))))
