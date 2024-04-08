(ns server.appointments.v1.db
  (:require [server.appointments.v1.queries :as queries.appointments]
            [server.db :as db]
            [server.util :as u]
            [medley.core :as medley])
  (:import (com.zaxxer.hikari
            HikariDataSource)))

(def ^:private allowed-keys
  [:fk-user-id
   :fk-event-id])

(defn get-by-id
  [^HikariDataSource ds id]
  (when id
    (->> (queries.appointments/make-get-by-id-query id)
         db/format
         (db/execute-one! ds))))

(defn list-appointments
  [^HikariDataSource ds query-params]
  (let [query (queries.appointments/make-list-query query-params)]
    (->> query
         db/format
         (db/execute! ds))))

(defn count-appointments
  [^HikariDataSource ds query-params]
  (->> (queries.appointments/make-count-query query-params)
       db/format
       (db/execute-one! ds)
       :count))

(defn create-appointment
  [^HikariDataSource ds data]
  (let [appointment-id (medley/random-uuid)
        prepared-data (-> data
                          (select-keys allowed-keys)
                          (assoc :id appointment-id)
                          (u/add-ns "appointment"))]
    (->> (queries.appointments/make-create-query prepared-data)
         db/format
         (db/execute-one! ds))
    {:appointment/id appointment-id}))

(defn delete-appointment
  [^HikariDataSource ds id]
  (when id
    (let [query (queries.appointments/make-delete-query id)]
      (->> query
           db/format
           (db/execute-one! ds)))))

(defn get-events-appointments
  [^HikariDataSource ds event-ids]
  (when (seq event-ids)
    (->> (queries.appointments/make-get-events-appointments-query event-ids)
         db/format
         (db/execute! ds))))
