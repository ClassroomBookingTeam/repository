(ns server.auth.authentication-cookie
  "Common routines for handling authentication cookie holdiing authentication info."
  (:require [buddy.sign.jwt :as jwt]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [ring.util.response]
            [superstring.core :as string]))

(def JWT_COOKIE_NAME
  "Name of the cookie holding the JWT authentication token."
  "jwt")

(def JWT_EXPIRY_TIME
  "default time in sec for JWT token expiry."
  (* 1000 60 60))

(def JWT_KEEP_ALIVE
  "default keep-alive time (time to refresh)."
  (* 30 60))

(s/fdef create
  :args (s/cat :user-id string?
               :jwt-secret string?)
  :ret (s/cat :name string?
              :value string?
              :options map?))

(defn create
  "Returns [name value options] representing the authentication cookie. These values can be
  passed directly to ring.util.response/set-cookie."
  [user-id jwt-secret]
  (let [expires (java.util.Date. (+ (* 1000 JWT_EXPIRY_TIME)
                                    (.getTime (java.util.Date.))))
        unzoned-dateformat (doto (java.text.SimpleDateFormat. "EEE, dd MMM yyyy HH:mm:ss Z")
                             (.setTimeZone (java.util.TimeZone/getTimeZone "GMT")))
        expires-str (.format unzoned-dateformat expires)
        value {:user-id user-id}]
    [JWT_COOKIE_NAME
     (jwt/sign value jwt-secret {:exp expires})
     {:expires expires-str
      :path "/"
      :secure false
      :http-only true
      :same-site :strict}]))

(s/fdef create-expired
  :args empty?
  :ret (s/cat :name string?
              :value string?
              :options map?))

(defn create-expired
  "Returns [:name :value :options] representing the authentication cookie. These values can be
  passed directly to ring.util.response/set-cookie to force cookie expiration."
  []
  [JWT_COOKIE_NAME
   nil
   {:expires "Thu, 01 Jan 1970 00:00:00 GMT"
    :path "/"}])

(defn get-jwt
  "Returns encoded jwt from `request`."
  [request]
  (or (get-in request [:cookies
                       JWT_COOKIE_NAME
                       :value])
      (:jwt request)
      (when-let [authorization (get (:headers request) "authorization")]
        (string/replace authorization "Bearer " ""))))


(defn get-authentication-info
  "Returns [:auth-user-code :auth-valid-till] if the user was previously authenticated and the
  `jwt` is a valid jwt, nil if there is no JWT cookie, it has expired or cannot be decoded with `jwt-secret`."
  [jwt jwt-secret]
  (let [decoded-jwt (try (jwt/unsign jwt jwt-secret)
                         (catch clojure.lang.ExceptionInfo _
                           ;; log and return nil
                           (log/trace "Received invalid jwt cookie" jwt)
                           nil))
        _ (log/trace "Decoded jwt" decoded-jwt)
        jwt-valid-till (:exp decoded-jwt)
        now (/ (.getTime (java.util.Date.)) 1000)
        authenticated? (and decoded-jwt (< now jwt-valid-till))]
    (when authenticated?
      {:auth-user-id (java.util.UUID/fromString (:user-id decoded-jwt))
       :auth-valid-till jwt-valid-till})))
