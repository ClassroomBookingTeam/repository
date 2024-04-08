(ns server.appointments.v1.api
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [ring.util.response :as rr]
            [server.appointments.v1.db :as db.appointments]
            [server.const :as const]
            [server.events.v1.db :as db.events]
            [server.spec :as spec]
            [server.users.v1.db :as db.users]))

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

(s/def :create-appointment/params
  (s/keys :req-un [#sdkw :appointment-param/event]))

(defn create-appointment
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)

        user-id (:auth-user-id ctx)
        event-id (:event data)

        prepared-data (assoc data
                             :fk-user-id user-id
                             :fk-event-id event-id)
        appointment-id (:appointment/id (db.appointments/create-appointment ds prepared-data))
        appointment (db.appointments/get-by-id ds appointment-id)
        user (db.users/get-by-id ds user-id)
        event (db.events/get-by-id ds event-id)]
    (log/info :msg "Создана запись на событие"
              :appointment appointment)
    (-> (rr/response (assoc appointment
                            :event event
                            :user user))
        (rr/status 201))))

(s/def :delete-appointment/params
  (s/keys :req-un [::spec/id]))

(defn delete-appointment
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)

        appointment-id (:id data)

        appointment (db.appointments/get-by-id ds appointment-id)]
    (if appointment
      (let [_ (db.appointments/delete-appointment ds appointment-id)
            event (db.events/get-by-id ds (:appointment/fk-event-id appointment))]
        (log/info :msg "Удалена запись на событие"
                  :appointment appointment)
        (-> (rr/response event)
            (rr/status 200)))
      (-> (rr/response [{:message "Запись не найдена"
                         :path "id"}])
          (rr/status 404)))))

(s/def :get-event-appointments/params
  (s/keys :req-un [::spec/id]))

(defn get-event-appointments
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)

        event-id (:id data)
        event-appointments (db.appointments/get-events-appointments ds #{event-id})]
    (-> (rr/response event-appointments)
        (rr/status 200))))
