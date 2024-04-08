(ns server.handler.middleware.auth
  "Authentication and authorisation for non view resources checker. Used to inject common staff during request processing."
  (:require [ring.util.response :as rr]
            [server.util :as u]))

(defn wrap-non-rpc-auth
  [handler]
  (fn [request]
    (let [auth-user-id (-> request
                           :auth-info
                           :auth-user-id)
          authenticated? (some? auth-user-id)]
      (cond
        (not authenticated?)
        (-> (rr/response "Unauthorized")
            (rr/status 401))

        :else (handler request)))))

(u/instrument-ns *ns*)
