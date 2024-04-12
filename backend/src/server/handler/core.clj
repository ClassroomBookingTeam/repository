(ns server.handler.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.tools.logging :as log]
   [compojure.core :as cc]
   [compojure.route :as cr]
   [muuntaja.format.edn]
   [muuntaja.format.json]
   [ring.middleware.cookies]
   [ring.middleware.cors :as middleware.cors]
   [ring.middleware.keyword-params]
   [ring.middleware.multipart-params]
   [ring.middleware.params]
   [ring.middleware.x-headers :as rx]
   [ring.util.response :as rr]
   [server.handler.middleware.ctx :as ctx]
   [server.handler.middleware.format :as format]
   [server.handler.middleware.jwt :as jwt]
   [server.handler.route.appointments :as route.appointments]
   [server.handler.route.buildings :as route.buildings]
   [server.handler.route.events :as route.events]
   [server.handler.route.recurrences :as route.recurrences]
   [server.handler.route.rooms :as route.rooms]
   [server.handler.route.session :as route.session]
   [server.handler.route.users :as route.users]
   [server.runtime :as rt]
   [server.util :as u]))

(defonce payload-logger
  (str "payload." *ns*))

(declare ^{:doc "Used to process servlet requests. Must be initialized on app startup."}
 ring-handler)

(defn bind-ring-handler
  [f]
  (def ring-handler f))

(defn wrap-logging
  "Returns a handler that logs request and response in debug mode."
  [handler]
  (if-not (u/str->bool (rt/env :payload-logger))
    handler
    (fn log-handler [request]
      (log/info payload-logger
                :trace nil
                (format "********* New HTTP request, uri: %s ********************\n%s\n"
                        (:uri request)
                        request))
      (let [response (handler request)]
        (log/info payload-logger
                  :trace nil
                  (format "********* Response to HTTP request, uri: %s ********************\n%s\n"
                          (:uri request)
                          response))
        response))))

;;
;; Dev Wrappers
;;

(defn wrap-uberwar
  "Returns a `handler` wrapped by default for uberwar profile middlewares."
  [handler]
  (-> handler
      (rx/wrap-xss-protection true)
      (middleware.cors/wrap-cors :access-control-allow-origin #".*"
                                 :access-control-allow-credentials "true"
                                 :access-control-allow-methods [:head
                                                                :get
                                                                :put
                                                                :post
                                                                :delete
                                                                :options])))

;;
;; Shutdown wrapper
;;

(defn wrap-shutdown-check
  "Returns a `handler` wrapped with a middleware that returns 503 if
   `server.runtime/shutting-down?` is set and `true`."
  [handler]
  (fn shutdown-check-fn
    [request]
    (if @rt/shutting-down?
      (rr/status
       (rr/response "*** Bye ***")
       503)
      (handler request))))

;;
;; Routes
;;

(defn make-routes
  "make routes"
  []
  (cc/routes
   route.events/routes-v1
   route.users/routes-v1
   route.buildings/routes-v1
   route.rooms/routes-v1
   route.recurrences/routes-v1
   route.session/routes-v1
   route.appointments/routes-v1

   (cr/not-found "not-found")))

;;
;; Handler
;;

(s/fdef make-base-handler
  :args (s/cat :opts (s/keys :req-un [::cfg
                                      ::pg-ds])))
(defn make-base-handler
  "Creates a base handler that can be further wrapped to provide specific
   functionality for different profiles.
   Returns: handler."
  [opts]
  (cond-> (make-routes)
    ;; trace logs
    :always (wrap-logging)
    ;; adds ctx info
    :always (ctx/wrap-ctx opts)
    ;; add auth-info key into request map if JWT is present in the header/cookie
    :always (jwt/wrap-jwt opts)
    ;; Format request/response
    :always (format/wrap-format)
    ;; convert map keys to keywords
    :always (ring.middleware.keyword-params/wrap-keyword-params)
    ;; extract query and form params
    :always (ring.middleware.multipart-params/wrap-multipart-params)
    :always (ring.middleware.params/wrap-params)
    ;; extract cookies
    :always (ring.middleware.cookies/wrap-cookies)
    ;; Checking shutting down
    :always (wrap-shutdown-check)))
