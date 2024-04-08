(ns server.users.v1.api
  (:require [buddy.hashers]
            [clojure.spec.alpha :as s]
            [ring.util.response :as rr]
            [server.auth.authentication-cookie :as auth-cookie]
            [server.spec :as spec]
            [server.users.v1.db :as db.users]))

(defn get-current-user
  [ctx user-id]
  (let [ds (:pg-ds ctx)
        user (db.users/get-by-id ds user-id)]
    (if user
      (-> (rr/response user)
          (rr/status 200))
      (rr/not-found []))))

(s/def #sdkw :user/password string?)
(s/def :session-check-user-data/params
  (s/keys :req-un [#sdkw :user/password
                   ::spec/email]))

(defn session-check-user-data
  [ctx request]
  (let [ds (:pg-ds ctx)
        data (:params request)
        {:keys [email password]} data

        user (db.users/get-by-email ds email)]
    (if-not user
      (-> (rr/response [{:message "Неправильный email или пароль"
                         :path ""}])
          (rr/status 403))
      (let [check-result (buddy.hashers/check password (:user/password user))]
        (if-not check-result
          (-> (rr/response [{:message "Неправильный email или пароль"
                             :path ""}])
              (rr/status 403))
          (apply rr/set-cookie
                 (-> (rr/response "OK")
                     (rr/status 200))
                 (auth-cookie/create (:user/id user) (:jwt-secret (:cfg ctx)))))))))
