(ns server.events.v1.api
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [medley.core :as medley]
            [ring.util.response :as rr]
            [server.appointments.v1.db :as db.appointments]
            [server.const :as const]
            [server.events.v1.db :as db.events]
            [server.spec :as spec]
            [server.time :as time]
            [superstring.core :as string]))

(defn- coerce-room-id
  [room-id]
  (when-not (string/blank? room-id)
    (->> (string/split room-id #",")
         (map (partial s/conform ::spec/id))
         (filter uuid?))))

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

(s/def :list-events/params
  (s/keys :opt-un [#sdkw :event-param/room-id
                   #sdkw :event-param/date-from
                   #sdkw :event-param/date-to
                   #sdkw :event-param/user-ids
                   #sdkw :event-param/search
                   #sdkw :event-param/building-id
                   ::spec/page
                   ::spec/page-size]))

(defn list-events
  [ctx request]
  (let [ds (:pg-ds ctx)
        request-data (:params request)
        current-user-id (:auth-user-id ctx)

        page (or (:page request-data) 1)
        page-size (:page-size request-data const/DEFAULT_QUERY_LIMIT)
        room-id (coerce-room-id (:room-id request-data))

        prepared-data {:room-id room-id
                       :user-ids (:user-ids request-data)
                       :search (:search request-data)
                       :building-id (:building-id request-data)
                       :date-from (:date-from request-data)
                       :date-to (:date-to request-data)
                       :offset (* page-size (dec page))
                       :limit page-size}

        events (db.events/list-events ds prepared-data)
        total-count (db.events/count-events ds prepared-data)
        event-ids (map :event/id events)
        event-id=>appointments (->> (db.appointments/get-events-appointments ds event-ids)
                                    (group-by :appointment/fk-event-id))

        response {:count total-count
                  :next (when (> total-count
                                 (* page page-size))
                          (inc page))
                  :previous (when (> page 1)
                              (dec page))
                  :results (reduce (fn [acc e]
                                     (let [appointments (event-id=>appointments (:event/id e))
                                           appointments-cnt (count appointments)
                                           max-appointments (:event/max-appointments e)
                                           remains (- max-appointments appointments-cnt)
                                           current-user-record? (->> appointments
                                                                     (filter #(= (:appointment/fk-user-id %)
                                                                                 current-user-id))
                                                                     first
                                                                     :appointment/id)]
                                       (conj acc (assoc e
                                                        :event/appointments-count appointments-cnt
                                                        :event/remaining-appointments remains
                                                        :event/appointment-id current-user-record?))))
                                   []
                                   events)}]
    (-> (rr/response response)
        (rr/status 200))))

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

(s/def :get-event/params
  (s/keys :req-un [::spec/id]))

(defn get-event
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)
        current-user-id (:auth-user-id ctx)
        event-id (:id data)
        event (db.events/get-by-id ds event-id)
        appointments (db.appointments/get-events-appointments ds #{event-id})
        appointments-cnt (count appointments)
        current-user-record? (->> appointments
                                  (filter #(= (:appointment/fk-user-id %)
                                              current-user-id))
                                  first
                                  :appointment/id)]
    (if event
      (-> (rr/response (assoc event
                              :event/appointments-count appointments-cnt
                              :event/appointment-id current-user-record?
                              :event/remaining-appointments (- (:event/max-appointments event)
                                                               appointments-cnt)))
          (rr/status 200))
      (-> (rr/response [{:message "Событие не найдено"
                         :path "id"}])
          (rr/status 404)))))

(s/def :delete-event/params
  (s/keys :req-un [::spec/id]))

(defn delete-event
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)
        event-id (:id data)
        event (db.events/get-by-id-light ds event-id)
        master-id (:event/master-id event)]
    (if event
      (do
        (log/info :msg "Удалено событие и все записи на него"
                  :event event)
        (db.events/delete-event ds event-id master-id)
        (-> (rr/response "Deleted")
          (rr/status 200)))
      (-> (rr/response [{:message "Событие не найдено"
                         :path "id"}])
          (rr/status 404)))))
