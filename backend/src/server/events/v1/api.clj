(ns server.events.v1.api
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [medley.core :as medley]
            [ring.util.response :as rr]
            [server.appointments.v1.db :as db.appointments]
            [server.events.v1.db :as db.events]
            [server.spec :as spec]
            [server.time :as time]))

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

(s/def :update-event/params
  (s/keys :req-un [::spec/id]
          :opt-un [#sdkw :event-param/room
                   #sdkw :event-param/date-from
                   #sdkw :event-param/date-to
                   #sdkw :event-param/recurrence
                   #sdkw :event-param/recurrence-until
                   #sdkw :event-param/max-appointments
                   ::title
                   ::description]))

(defn update-event
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)
        event-id (:id data)
        current-user-id (:auth-user-id ctx)

        event (db.events/get-by-id-light ds event-id)
        old-recurrence-id (:event/fk-recurrence-id event)
        new-recurrence-id (:recurrence data)
        old-recurrence-until (:event/recurrence-until event)
        new-recurrence-until (some-> data
                                     :recurrence-until
                                     .toInstant)]
    (cond
      (not= old-recurrence-id new-recurrence-id)
      (-> (rr/response [{:message "Запрещено менять метод повторения"
                         :path "recurrence"}])
          (rr/status 400))

      (or (and new-recurrence-until
               (nil? old-recurrence-id))

          (and (nil? new-recurrence-until)
               old-recurrence-id)

          (and new-recurrence-until
               old-recurrence-id
               (not (time/equal? old-recurrence-until new-recurrence-until))))
      (-> (rr/response [{:message "Запрещено менять дату окончания повторений"
                         :path "recurrence_until"}])
          (rr/status 400))

      event
      (let [prepared-data (medley/assoc-some data
                                             :fk-room-id (:room data)
                                             :fk-recurrence-id (:recurrence data))
            _ (db.events/update-event ds prepared-data)
            event (db.events/get-by-id ds event-id)
            appointments (db.appointments/get-events-appointments ds #{event-id})
            current-user-record? (->> appointments
                                      (filter #(= (:appointment/fk-user-id %)
                                                  current-user-id))
                                      first
                                      :appointment/id)
            appointments-cnt (count appointments)]
        (log/info :msg "Обновлено событие"
                  :result event)
        (-> (rr/response (assoc event
                                :event/appointments-count appointments-cnt
                                :event/appointment-id current-user-record?
                                :event/remaining-appointments (- (:event/max-appointments event)
                                                                 appointments-cnt)))
            (rr/status 201)))

      :else
      (-> (rr/response [{:message "Событие не найдено"
                         :path "id"}])
          (rr/status 404)))))
