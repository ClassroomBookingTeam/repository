(ns server.rooms.v1.db
  (:require [server.db :as db]
            [server.rooms.v1.queries :as queries.rooms])
  (:import (com.zaxxer.hikari
            HikariDataSource)))

;; Helpers

(defn- assoc-building-data
  [x]
  (-> x
      (assoc :room/building (select-keys x [:building/id :building/name]))
      (dissoc :building/id :building/name)))

;; Main fns

(defn list-rooms
  [^HikariDataSource ds query-params]
  (let [query (queries.rooms/make-list-query query-params)]
   (->> query
        db/format
        (db/execute! ds)
        (mapv assoc-building-data))))

(defn count-rooms
  [^HikariDataSource ds query-params]
  (let [query (-> (queries.rooms/make-list-query query-params)
                  (assoc :select [:%count.r.*])
                  (dissoc :limit
                          :group-by
                          :offset
                          :order-by))]
    (->> query
         db/format
         (db/execute-one! ds)
         :count)))

(defn get-by-id
  [^HikariDataSource ds id]
  (when id
    (let [query (queries.rooms/make-get-by-id-query id)]
      (->> query
           db/format
           (db/execute-one! ds)
           assoc-building-data))))
