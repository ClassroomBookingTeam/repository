(ns server.handler.middleware.jwt
  (:require [server.auth.authentication-cookie :as auth-cookie]))

(defn wrap-jwt
  "Wraps request handler, associates :auth-info key into request map with value -
  map of auth-user-code & auth-valid-till keys."
  [f {:keys [cfg]}]
  (let [{:keys [jwt-secret enabled-credential-verifiers]} cfg
        request->jwt (if (contains? enabled-credential-verifiers :default)
                       auth-cookie/get-jwt
                       (constantly nil))]
    (fn [request]
      (f
       (assoc request
              :auth-info
              (condp apply [request]
                request->jwt :>> #(auth-cookie/get-authentication-info % jwt-secret)
                nil))))))