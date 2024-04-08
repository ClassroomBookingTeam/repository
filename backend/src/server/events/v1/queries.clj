(ns server.events.v1.queries
  (:require [honeysql.helpers :as sql.helpers]
            [server.db :as db]
            [superstring.core :as string]))

(def ^:private full-name
  (db/as-raw "concat_ws(' ',
                        u.last_name,
                        u.first_name,
                        u.middle_name)"))

(def ^:private short-name
  (db/as-raw "concat_ws(' ',
                        u.last_name,
                        concat(substring(u.first_name, 1, 1), '.'),
                        CASE
                          WHEN middle_name IS NULL THEN NULL
                          ELSE concat(substring(u.middle_name, 1, 1), '.')
                        END)"))

(defn make-list-query
  [{:keys [room-id
           search
           building-id
           user-ids
           date-from
           date-to
           offset
           limit]}]
  (cond-> {:select [:e.id
                    :e.title
                    :e.description
                    :e.recurrence-until
                    :e.date-from
                    :e.date-to
                    :e.max-appointments
                    :e.created-at
                    :u.id
                    :u.first-name
                    :u.last-name
                    :u.middle-name
                    :u.email
                    :b.id
                    :b.name
                    :room.id
                    :room.number
                    :room.floor
                    :rec.id
                    :rec.name
                    [(db/as-raw "array_agg(r.name)") :roles]
                    [short-name :short-name]
                    [full-name :full-name]]
           :modifiers [:distinct]
           :from [[:classroombooking.event :e]]
           :left-join [[:classroombooking.user :u] [:= :u.id :e.fk-user-id]
                       [:classroombooking.permission :p] [:= :p.fk-user-id :u.id]
                       [:classroombooking.role :r] [:= :r.id :p.fk-role-id]
                       [:classroombooking.room :room] [:= :room.id :e.fk-room-id]
                       [:classroombooking.recurrence :rec] [:= :rec.id :e.fk-recurrence-id]
                       [:classroombooking.building :b] [:= :b.id :room.fk-building-id]]
           :order-by [[:e.date-from :asc]]
           :group-by [:e.id
                      :u.id
                      :b.id
                      :room.id
                      :rec.id]}
    limit (sql.helpers/limit limit)
    offset (sql.helpers/offset offset)

    (seq room-id) (sql.helpers/merge-where [:in :room.id (set room-id)])
    (seq user-ids) (sql.helpers/merge-where [:in :e.fk-user-id (seq user-ids)])

    (not (string/blank? search)) (sql.helpers/merge-where :or
                                                          [:like (db/as-call :lower full-name) (str "%" (string/lower-case search) "%")]
                                                          [:like (db/as-call :lower :e.title) (str "%" (string/lower-case search) "%")])
    building-id (sql.helpers/merge-where [:= :b.id building-id])
    date-from (sql.helpers/merge-where [:>= :e.date-from date-from])
    date-to (sql.helpers/merge-where [:<= :e.date-to date-to])))

(defn make-count-query
  [{:keys [room-id
           building-id
           search
           user-ids
           date-from
           date-to]}]
  (cond-> {:select [:%count.e.*]
           :from [[:classroombooking.event :e]]}
    (seq room-id) (sql.helpers/merge-where [:in :room.id (set room-id)])
    (or (seq room-id)
        building-id) (sql.helpers/merge-left-join [:classroombooking.room :room] [:= :room.id :e.fk-room-id])

    (or (seq user-ids)
        (not (string/blank? search)))
    (sql.helpers/merge-left-join [:classroombooking.user :u] [:= :u.id :e.fk-user-id])
    (seq user-ids) (sql.helpers/merge-where [:in :e.fk-user-id (seq user-ids)])

    (not (string/blank? search)) (sql.helpers/merge-where :or
                                                          [:like (db/as-call :lower full-name) (str "%" (string/lower-case search) "%")]
                                                          [:like (db/as-call :lower :e.title) (str "%" (string/lower-case search) "%")])

    building-id (sql.helpers/merge-where [:= :b.id building-id])
    building-id (sql.helpers/merge-left-join [:classroombooking.building :b] [:= :b.id :room.fk-building-id])

    date-from (sql.helpers/merge-where [:>= :e.date-from date-from])
    date-to (sql.helpers/merge-where [:<= :e.date-to date-to])))

(defn make-light-get-by-id-query
  [id]
  {:select [:e.id
            :e.master-id
            :e.fk-recurrence-id
            :e.recurrence-until]
   :from [[:classroombooking.event :e]]
   :where [:= :e.id id]})

(defn make-get-by-id-query
  [id]
  {:select [:e.*
            :u.*
            :b.*
            :room.*
            :rec.*
            [(db/as-raw "array_agg(r.name)") :roles]
            [short-name :short-name]
            [full-name :full-name]]
   :from [[:classroombooking.event :e]]
   :left-join [[:classroombooking.user :u] [:= :u.id :e.fk-user-id]
               [:classroombooking.permission :p] [:= :p.fk-user-id :u.id]
               [:classroombooking.role :r] [:= :r.id :p.fk-role-id]
               [:classroombooking.room :room] [:= :room.id :e.fk-room-id]
               [:classroombooking.recurrence :rec] [:= :rec.id :e.fk-recurrence-id]
               [:classroombooking.building :b] [:= :b.id :room.fk-building-id]]
   :where [:= :e.id id]
   :group-by [:e.id
              :u.id
              :b.id
              :room.id
              :rec.id]})

(defn make-create-query
  [values]
  {:insert-into :classroombooking.event
   :values values})

(defn make-delete-query
  [id master-id]
  {:delete-from :classroombooking.event
   :where (if master-id
            [:or
             [:= :classroombooking.event.id master-id]
             [:= :classroombooking.event.master-id master-id]]
            [:or
             [:= :classroombooking.event.id id]
             [:= :classroombooking.event.master-id id]])})
