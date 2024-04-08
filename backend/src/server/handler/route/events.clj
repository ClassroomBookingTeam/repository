(ns server.handler.route.events
  (:require [compojure.core :as cc]
            [ring.util.response :as rr]
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
              (v1.api/update-event ctx request)))))

      (cc/wrap-routes middleware.auth/wrap-non-rpc-auth)
      (cc/wrap-routes middleware.safety-wrapper/wrap-with-safety-wrapper)))
