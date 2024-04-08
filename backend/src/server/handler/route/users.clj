(ns server.handler.route.users
  (:require [compojure.core :as cc]
            [server.handler.middleware.auth :as middleware.auth]
            [server.handler.middleware.safety-wrapper :as middleware.safety-wrapper]
            [server.runtime :as rt]
            [server.users.v1.api :as v1.api]))

(set! *warn-on-reflection* true)

;;
;; Routes
;;

(cc/defroutes routes-v1
  (-> (cc/context "/v1/users/current" []
        (cc/GET "/" []
          (let [ctx rt/*ctx*]
            (v1.api/get-current-user ctx (:auth-user-id rt/*ctx*)))))
      (cc/wrap-routes middleware.auth/wrap-non-rpc-auth)
      (cc/wrap-routes middleware.safety-wrapper/wrap-with-safety-wrapper)))
