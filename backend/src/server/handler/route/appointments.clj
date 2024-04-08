(ns server.handler.route.appointments
  (:require [clojure.tools.logging :as log]
            [compojure.core :as cc]
            [ring.util.response :as rr]
            [server.appointments.v1.api :as v1.api]
            [server.handler.middleware.auth :as middleware.auth]
            [server.handler.middleware.safety-wrapper :as middleware.safety-wrapper]
            [server.runtime :as rt]
            [server.util :as u]))

(set! *warn-on-reflection* true)

;;
;; Routes
;;

(cc/defroutes routes-v1
  (-> (cc/context "/v1/appointments" []
        (cc/POST "/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :create-appointment/params
                                           :request request})]
            (log/info :msg "Вызов создания записи"
                      :auth-user-id (:auth-user-id ctx)
                      :params (:params request)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api/create-appointment ctx request)))))
      (cc/wrap-routes middleware.auth/wrap-non-rpc-auth)
      (cc/wrap-routes middleware.safety-wrapper/wrap-with-safety-wrapper)))
