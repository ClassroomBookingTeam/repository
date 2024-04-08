(ns server.rooms.v1.api
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :as rr]
            [server.const :as const]
            [server.rooms.v1.db :as db.rooms]
            [server.spec :as spec]))

(s/def :get-room/params
  (s/keys :req-un [::spec/id]))

(defn get-room
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)
        room-id (:id data)
        room (db.rooms/get-by-id ds room-id)]
    (if room
      (-> (rr/response room)
          (rr/status 200))
      (-> (rr/response (format "Room with id %s not found" room-id))
          (rr/status 404)))))

(s/def :get-building-rooms/params
  (s/keys :req-un [::spec/id]
          :opt-un [::spec/page
                   ::spec/page-size
                   ::spec/available-at]))

(defn get-building-rooms
  [ctx request]
  (let [ds (:pg-ds ctx)
        request-data (:params request)

        page (or (:page request-data) 1)
        page-size (:page-size request-data const/DEFAULT_QUERY_LIMIT)

        prepared-data {:building-id (:id request-data)
                       :available-at (:available-at request-data)
                       :offset (* page-size (dec page))
                       :limit page-size}

        building-rooms (db.rooms/list-rooms ds prepared-data)
        total-count (db.rooms/count-rooms ds prepared-data)

        response {:count total-count
                  :next (when (> total-count
                                 (* page page-size))
                          (inc page))
                  :previous (when (> page 1)
                              (dec page))
                  :results building-rooms}]
    (-> (rr/response response)
        (rr/status 200))))
