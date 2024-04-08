(ns server.events.v1.api
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [medley.core :as medley]
            [ring.util.response :as rr]
            [server.events.v1.db :as db.events]
            [server.spec :as spec]))

(s/def #sdkw :event-param/room-id string?)
(s/def #sdkw :event-param/room ::spec/id)
(s/def #sdkw :event-param/date-from ::spec/->inst)
(s/def #sdkw :event-param/date-to ::spec/->inst)
(s/def #sdkw :event-param/recurrence-until ::spec/->inst)
(s/def #sdkw :event-param/recurrence ::spec/id)
(s/def #sdkw :event-param/max-appointments ::spec/->int)
(s/def #sdkw :event-param/user-ids (s/coll-of ::spec/id))
(s/def #sdkw :event-param/search string?)
(s/def #sdkw :event-param/building-id ::spec/id)

(s/def :create-event/params
  (s/keys :req-un [#sdkw :event-param/room
                   #sdkw :event-param/date-from
                   #sdkw :event-param/date-to]
          :opt-un [#sdkw :event-param/recurrence
                   #sdkw :event-param/recurrence-until
                   #sdkw :event-param/max-appointments
                   ::title
                   ::description]))

(defn create-event
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)

        recurrence-id (:recurrence data)
        recurrence-until (:recurrence-until data)]
    (cond
      (and (some? recurrence-id)
           (nil? recurrence-until))
      (-> (rr/response [{:message "Укажите дату окончания повторения"
                         :path "recurrence_until"}])
          (rr/status 400))

      :else
      (let [prepared-data (medley/assoc-some data
                                             :fk-user-id (:auth-user-id ctx)
                                             :fk-room-id (:room data)
                                             :fk-recurrence-id recurrence-id)
            event-id (:event/id (db.events/create-event ds prepared-data))
            event (db.events/get-by-id ds event-id)]
        (log/info :msg "Создано событие"
                  :result event)
        (-> (rr/response (assoc event
                                :event/appointments-count 0
                                :event/remaining-appointments (:event/max-appointments event)))
            (rr/status 201))))))
