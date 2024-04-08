(ns server.handler.route.events
  (:require [compojure.core :as cc]
            [ring.util.response :as rr]
            [server.appointments.v1.api :as appointments.v1.api]
            [server.events.v1.api :as v1.api]
            [server.handler.middleware.auth :as middleware.auth]
            [server.handler.middleware.safety-wrapper :as middleware.safety-wrapper]
            [server.runtime :as rt]
            [server.util :as u]
            [clojure.tools.logging :as log]))

(set! *warn-on-reflection* true)

;;
;; Routes
;;

(cc/defroutes routes-v1
  "Routes for getting or downloading arbitrary blobs by uid or file name."
  (-> (cc/context "/v1/events" []
        (cc/GET "/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :list-events/params
                                           :request request})]
            (log/info :msg "Вызов получения списка событий"
                      :auth-user-id (:auth-user-id ctx)
                      :params (:params request)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api/list-events ctx request))))

        (cc/POST "/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :create-event/params
                                           :request request})]
            (log/info :msg "Вызов создания события"
                      :auth-user-id (:auth-user-id ctx)
                      :params (:params request)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api/create-event ctx request))))

        (cc/GET "/:id/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :get-event/params
                                           :request request})]
            (log/info :msg "Вызов получения события"
                      :auth-user-id (:auth-user-id ctx)
                      :params (:params request)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api/get-event ctx request))))

        (cc/PATCH "/:id/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :update-event/params
                                           :request request})]
            (log/info :msg "Вызов обновления события"
                      :auth-user-id (:auth-user-id ctx)
                      :params (:params request)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api/update-event ctx request))))

        (cc/DELETE "/:id/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :delete-event/params
                                           :request request})]
            (log/info :msg "Вызов удаления события"
                      :auth-user-id (:auth-user-id ctx)
                      :params (:params request)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api/delete-event ctx request))))

        (cc/GET "/:id/appointments/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :get-event-appointments/params
                                           :request request})]
            (log/info :msg "Вызов получения списка записей на событие"
                      :auth-user-id (:auth-user-id ctx)
                      :params (:params request)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (appointments.v1.api/get-event-appointments ctx request)))))

      (cc/wrap-routes middleware.auth/wrap-non-rpc-auth)
      (cc/wrap-routes middleware.safety-wrapper/wrap-with-safety-wrapper)))
