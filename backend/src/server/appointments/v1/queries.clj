(ns server.appointments.v1.queries
  (:require [honeysql.helpers :as sql.helpers]))

(defn make-list-query
  [{:keys [user-id
           date-from
           date-to
           offset
           limit]}]
  (cond-> {:select [:a.*]
           :from [[:classroombooking.appointment :a]]
           :order-by [[:a.created-at :asc]]}
    limit (sql.helpers/limit limit)
    offset (sql.helpers/offset offset)

    user-id (sql.helpers/merge-where [:= :a.fk-user-id user-id])

    date-from (sql.helpers/merge-where [:>= :a.created-at date-from])
    date-to (sql.helpers/merge-where [:<= :a.created-at date-to])))

(defn make-count-query
  [{:keys [user-id
           date-from
           date-to]}]
  (cond-> {:select [:%count.a.*]
           :from [[:classroombooking.appointment :a]]}
    user-id (sql.helpers/merge-where [:= :a.fk-user-id user-id])
    date-from (sql.helpers/merge-where [:>= :a.created-at date-from])
    date-to (sql.helpers/merge-where [:<= :a.created-at date-to])))

(defn make-get-by-id-query
  [id]
  {:select [:*]
   :from [:classroombooking.appointment]
   :where [:= :classroombooking.appointment.id id]})

(defn make-create-query
  [values]
  {:insert-into :classroombooking.appointment
   :values [values]})

(defn make-delete-query
  [id]
  {:delete-from :classroombooking.appointment
   :where [:= :classroombooking.appointment.id id]})

(defn make-get-events-appointments-query
  [event-ids]
  {:select [:*]
   :from [:classroombooking.appointment]
   :where [:in :classroombooking.appointment.fk-event-id (set event-ids)]})
