(ns server.appointments.v1.api
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :as rr]
            [server.appointments.v1.db :as db.appointments]
            [server.const :as const]
            [server.events.v1.db :as db.events]
            [server.spec :as spec]))

(s/def #sdkw :appointment-param/event ::spec/id)
(s/def #sdkw :appointment-param/date-from ::spec/->inst)
(s/def #sdkw :appointment-param/date-to ::spec/->inst)
(s/def #sdkw :appointment-param/user-id ::spec/->uuid)

(s/def :list-appointments/params
  (s/keys :opt-un [#sdkw :appointment-param/date-from
                   #sdkw :appointment-param/date-to
                   #sdkw :appointment-param/user-id
                   ::spec/page
                   ::spec/page-size]))

(defn list-appointments
  [ctx request]
  (let [ds (:pg-ds ctx)
        request-data (:params request)

        page (or (:page request-data) 1)
        page-size (:page-size request-data const/DEFAULT_QUERY_LIMIT)

        prepared-data {:user-id (:user-id request-data)
                       :date-from (:date-from request-data)
                       :date-to (:date-to request-data)
                       :offset (* page-size (dec page))
                       :limit page-size}

        appointments (reduce (fn [acc a]
                               (let [event (db.events/get-by-id ds (:appointment/fk-event-id a))]
                                 (conj acc (assoc event
                                                  :event (assoc event :appointment-id (:appointment/id a))))))
                             []
                             (db.appointments/list-appointments ds prepared-data))
        total-count (db.appointments/count-appointments ds prepared-data)

        response {:count total-count
                  :next (when (> total-count
                                 (* page page-size))
                          (inc page))
                  :previous (when (> page 1)
                              (dec page))
                  :results appointments}]
    (-> (rr/response response)
        (rr/status 200))))

