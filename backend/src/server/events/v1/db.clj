(ns server.events.v1.db
  (:require [medley.core :as medley]
            [server.db :as db]
            [server.events.v1.queries :as queries.events]
            [server.recurrences.v1.db :as db.recurrences]
            [server.time :as time]
            [server.util :as u])
  (:import (com.zaxxer.hikari
            HikariDataSource)))

(def ^:private allowed-keys
  [:title
   :description
   :date-from
   :date-to
   :recurrence-until
   :max-appointments
   :fk-recurrence-id
   :fk-user-id
   :fk-room-id])

(defn- transform-aux-data
  [x]
  (let [building-keys-set #{:building/id
                            :building/name}
        user-keys-set #{:user/id
                        :user/first-name
                        :user/last-name
                        :user/middle-name
                        :user/email
                        :roles
                        :short-name
                        :full-name}
        room-keys-set #{:room/id
                        :room/number
                        :room/floor}
        recurrence-keys-set #{:recurrence/id
                              :recurrence/name}
        event-keys-set #{:event/id
                         :event/title
                         :event/description
                         :event/recurrence-until
                         :event/date-from
                         :event/date-to
                         :event/max-appointments}]
    (-> (select-keys x event-keys-set)
        (assoc :event/user (select-keys x user-keys-set)
               :event/room (select-keys x room-keys-set)
               :event/recurrence (select-keys x recurrence-keys-set))
        (update-in [:event/user :roles] (fn [roles]
                                          (some->> roles
                                                   .getArray
                                                   (into [])
                                                   (filter some?)
                                                   set)))
        (assoc-in [:event/room :building] (select-keys x building-keys-set)))))

(defn list-events
  [^HikariDataSource ds query-params]
  (let [query (queries.events/make-list-query query-params)]
    (->> query
         db/format
         (db/execute! ds)
         (mapv transform-aux-data))))

(defn count-events
  [^HikariDataSource ds query-params]
  (->> (queries.events/make-count-query query-params)
       db/format
       (db/execute-one! ds)
       :count))

(defn get-by-id
  [^HikariDataSource ds id]
  (when id
    (->> (queries.events/make-get-by-id-query id)
         db/format
         (db/execute-one! ds)
         transform-aux-data)))

(defn get-by-id-light
  [^HikariDataSource ds id]
  (when id
    (->> (queries.events/make-light-get-by-id-query id)
         db/format
         (db/execute-one! ds))))

(defn- make-reccurenced-events
  [ds data]
  (let [recurrence-id (:event/fk-recurrence-id data)
        event-id (:event/id data)
        event-start-date (:event/date-from data)
        event-end-date (:event/date-to data)
        until (:event/recurrence-until data)
        recurrence (->> (db.recurrences/list-recurrences ds)
                        (medley/find-first #(= (:recurrence/id %) recurrence-id))
                        :recurrence/name)
        add-fn (condp = recurrence
                 "Каждый день" #(-> %
                                    time/inst->local-date-time
                                    (time/plus-days 1)
                                    time/local-date-time->inst)
                 "Каждую неделю" #(-> %
                                      time/inst->local-date-time
                                      (time/plus-days 7)
                                      time/local-date-time->inst)
                 "Каждые две недели" #(-> %
                                          time/inst->local-date-time
                                          (time/plus-days 14)
                                          time/local-date-time->inst)
                 "Каждый месяц" #(-> %
                                     time/inst->local-date-time
                                     (time/plus-months 1)
                                     time/local-date-time->inst))]
    (loop [start-date (add-fn event-start-date)
           end-date (add-fn event-end-date)
           acc []]
      (if (time/after? start-date until)
        acc
        (recur
         (add-fn start-date)
         (add-fn end-date)
         (conj acc (assoc data
                          :event/id (medley/random-uuid)
                          :event/master-id event-id
                          :event/date-from start-date
                          :event/date-to end-date)))))))

(defn create-event
  [^HikariDataSource ds data]
  (let [event-id (medley/random-uuid)
        prepared-data (-> data
                          (select-keys allowed-keys)
                          (u/add-ns "event")
                          (assoc :event/id event-id
                                 :event/master-id nil))
        prepared-data' (cond-> [prepared-data]
                         (some? (:event/fk-recurrence-id prepared-data))
                         (into (make-reccurenced-events ds prepared-data)))]
    (->> (queries.events/make-create-query prepared-data')
         db/format
         (db/execute-one! ds))
    {:event/id event-id}))

(defn update-event
  [^HikariDataSource ds data]
  (when-let [id (:id data)]
    (let [prepared-data (-> data
                            (select-keys allowed-keys)
                            (u/add-ns "event")
                            (dissoc :event/id))]
      (->> {:update :classroombooking.event
            :set prepared-data
            :where [:= :classroombooking.event.id id]}
           db/format
           (db/execute-one! ds)))))

(defn delete-event
  [^HikariDataSource ds id master-id]
  (when id
    (let [query (queries.events/make-delete-query id master-id)]
      (->> query
           db/format
           (db/execute-one! ds)))))
