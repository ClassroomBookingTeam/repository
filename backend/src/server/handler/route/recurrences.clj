(ns server.handler.route.recurrences
  (:require [compojure.core :as cc]
            [server.handler.middleware.auth :as middleware.auth]
            [server.handler.middleware.safety-wrapper :as middleware.safety-wrapper]
            [server.recurrences.v1.api :as v1.api]
            [server.runtime :as rt]))

(set! *warn-on-reflection* true)

;;
;; Routes
;;

(cc/defroutes routes-v1
  (-> (cc/context "/v1/recurrences" []
        (cc/GET "/" []
          (let [ctx rt/*ctx*]
            (v1.api/list-recurrences ctx nil))))

      (cc/wrap-routes middleware.auth/wrap-non-rpc-auth)
      (cc/wrap-routes middleware.safety-wrapper/wrap-with-safety-wrapper)))
