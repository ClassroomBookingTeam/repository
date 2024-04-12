(ns server.handler.route.buildings
  (:require [clojure.tools.logging :as log]
            [compojure.core :as cc]
            [ring.util.response :as rr]
            [server.buildings.v1.api :as v1.api]
            [server.handler.middleware.auth :as middleware.auth]
            [server.handler.middleware.safety-wrapper :as middleware.safety-wrapper]
            [server.rooms.v1.api :as rooms.v1.api]
            [server.runtime :as rt]
            [server.util :as u]))

(set! *warn-on-reflection* true)

;;
;; Routes
;;

(cc/defroutes routes-v1
  (-> (cc/context "/v1/buildings" []
        (cc/GET "/" []
          (let [ctx rt/*ctx*]
            (v1.api/list-buildings ctx nil)))

        (cc/GET "/:id/rooms/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :get-building-rooms/params
                                           :request request})]
            (log/info :msg "Вызов получения аудиторий корпуса"
                      :auth-user-id (:auth-user-id ctx)
                      :params (:params request)
                      :errors errors)
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (rooms.v1.api/get-building-rooms ctx request)))))

      (cc/wrap-routes middleware.auth/wrap-non-rpc-auth)
      (cc/wrap-routes middleware.safety-wrapper/wrap-with-safety-wrapper)))
