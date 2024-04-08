(ns server.rooms.v1.queries
  (:require [honeysql.helpers :as sql.helpers]))

(defn make-list-query
  [{:keys [available-at
           building-id
           limit
           offset]}]
  (cond-> {:select [:r.*
                    :b.id
                    :b.name]
           :from [[:classroombooking.room :r]]
           :left-join [[:classroombooking.building :b] [:= :b.id :r.fk-building-id]]
           :order-by [[:b.name]
                      [:r.floor :asc]
                      [:r.number :asc]]
           :group-by [:r.id
                      :b.id
                      :b.name]}
    limit (sql.helpers/limit limit)
    offset (sql.helpers/offset offset)

    building-id (sql.helpers/merge-where [:= :b.id building-id])

    available-at (sql.helpers/merge-left-join [:classroombooking.event :e] [:= :r.id :e.fk-room-id])
    available-at (sql.helpers/merge-where [:not [:between available-at :e.date-from :e.date-to]])))

(defn make-get-by-id-query
  [id]
  {:select [:r.*
            :b.id
            :b.name]
   :from [[:classroombooking.room :r]]
   :left-join [[:classroombooking.building :b] [:= :b.id :r.fk-building-id]]
   :where [:= :r.id id]})
