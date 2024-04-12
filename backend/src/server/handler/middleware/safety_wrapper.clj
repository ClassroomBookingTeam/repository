(ns server.handler.middleware.safety-wrapper
  (:require [clojure.tools.logging :as log]
            [ring.util.response :as rr]
            [server.util :as u]))

(defn wrap-with-safety-wrapper
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (log/error :e e)
        (-> (rr/response [{:message (ex-message e)
                           :path ""
                           :data (ex-data e)}])
            (rr/status 500))))))

(u/instrument-ns *ns*)
