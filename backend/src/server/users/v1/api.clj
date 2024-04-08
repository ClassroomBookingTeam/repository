(ns server.users.v1.api
  (:require [buddy.hashers]
            [ring.util.response :as rr]
            [server.auth.authentication-cookie :as auth-cookie]
            [server.users.v1.db :as db.users]))

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
