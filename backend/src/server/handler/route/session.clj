(ns server.handler.route.session
  (:require [compojure.core :as cc]
            [ring.util.response :as rr]
            [server.handler.middleware.safety-wrapper :as middleware.safety-wrapper]
            [server.runtime :as rt]
            [server.users.v1.api :as v1.api]
            [server.util :as u]))

(set! *warn-on-reflection* true)

;;
;; Routes
;;

(cc/defroutes routes-v1
  (-> (cc/context "/v1/session" []
        (cc/POST "/" {:as request}
          (let [ctx rt/*ctx*

                {:keys [errors request]}
                (u/conform-request-params {:spec :session-check-user-data/params
                                           :request request})]
            (if (seq errors)
              (-> (rr/response errors)
                  (rr/status 400))
              (v1.api/session-check-user-data ctx request)))))

      (cc/wrap-routes middleware.safety-wrapper/wrap-with-safety-wrapper)))
