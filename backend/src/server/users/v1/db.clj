(ns server.users.v1.db
  (:require [clojure.set :as cset]
            [server.db :as db])
  (:import (com.zaxxer.hikari
            HikariDataSource)))

(def ^:private full-name
  (db/as-raw "concat_ws(' ',
                        last_name,
                        first_name,
                        middle_name)"))

(def ^:private short-name
  (db/as-raw "concat_ws(' ',
                        last_name,
                        concat(substring(first_name, 1, 1), '.'),
                        CASE
                          WHEN middle_name IS NULL THEN NULL
                          ELSE concat(substring(middle_name, 1, 1), '.')
                        END)"))

(defn get-by-id
  [^HikariDataSource ds id]
  (when id
    (->> {:select [:u.id
                   :u.email
                   :u.first-name
                   :last-name
                   :middle-name
                   [(db/as-raw "json_agg(r.name)") :roles]
                   [short-name :short-name]
                   [full-name :full-name]]
          :from [[:classroombooking.user :u]]
          :left-join [[:classroombooking.permission :p] [:= :p.fk-user-id :u.id]
                      [:classroombooking.role :r] [:= :r.id :p.fk-role-id]]
          :where [:= :u.id id]
          :group-by [:u.id]}
         db/format
         (db/execute-one! ds)
         (#(cset/rename-keys % {:short-name :user/short-name
                                :full-name :user/full-name
                                :roles :user/roles})))))

(defn get-by-email
  [^HikariDataSource ds email]
  (when email
    (->> {:select [:u.*]
          :from [[:classroombooking.user :u]]
          :where [:= :u.email email]}
         db/format
         (db/execute-one! ds))))
