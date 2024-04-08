(ns server.handler.route.users
  (:require [compojure.core :as cc]
            [ring.util.response :as rr]
            [server.appointments.v1.api :as v1.api.appointments]
            [server.events.v1.api :as v1.api.events]
            [server.handler.middleware.auth :as middleware.auth]
            [server.handler.middleware.safety-wrapper :as middleware.safety-wrapper]
            [server.runtime :as rt]
            [server.users.v1.api :as v1.api]
            [server.util :as u]
            [clojure.tools.logging :as log]))

(set! *warn-on-reflection* true)

;;
;; Routes
;;

(cc/defroutes routes-v1
  (-> (cc/context "/v1/users/current" []
        (cc/GET "/" []
          (let [ctx rt/*ctx*]
            (v1.api/get-current-user ctx (:auth-user-id rt/*ctx*))))

        (cc/GET "/events/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :list-events/params
                                           :request request})]
            (log/info :msg "Вызов получения списка событий пользователя"
                      :params (:params request)
                      :auth-user-id (:auth-user-id ctx)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api.events/list-events ctx
                                         (assoc-in request
                                                   [:params :user-ids] #{(:auth-user-id ctx)})))))
        (cc/GET "/appointments/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :list-appointments/params
                                           :request request})]
            (log/info :msg "Вызов получения списка записей пользователя"
                      :params (:params request)
                      :auth-user-id (:auth-user-id ctx)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api.appointments/list-appointments ctx
                                                     (assoc-in request
                                                               [:params :user-id] (:auth-user-id ctx)))))))
      (cc/wrap-routes middleware.auth/wrap-non-rpc-auth)
      (cc/wrap-routes middleware.safety-wrapper/wrap-with-safety-wrapper)))
