(ns server.core
  (:require [clojure.tools.logging :as log]
            [ring.adapter.jetty]
            [server.const :as const]
            [server.runtime :as rt]
            [server.tagged-literals])
  (:import (org.eclipse.jetty.server.handler ContextHandler ContextHandlerCollection)
           (org.eclipse.jetty.rewrite.handler RewritePatternRule RewriteHandler))
  (:gen-class))

(def ^:dynamic *jetty-port*
  "Dynamic var holding Jetty port."
  const/DEFAULT_JETTY_PORT)

(defn- log-env-and-properties
  "Outputs environment and properties info to log."
  []
  (log/info (apply str (concat (repeat 40 "*")
                               [" ENVIRONMENT "]
                               (concat (repeat 40 "*")))))
  (log/info "uncomment me in core.clj")
  #_(doseq [[k v] (System/getenv)]
      (log/info k v))

  (log/info (apply str (concat (repeat 40 "*")
                               [" JAVA PROPERTIES "]
                               (concat (repeat 40 "*")))))

  (log/info "uncomment me in core.clj")
  #_(doseq [[k v] (System/getProperties)]
      (log/info k v)))

(defn- configurator
  "Performs jetty `server` configuration based on `cfg`:
   creates context, sets context path, and wraps current handler."
  [cfg server]
  (let [base-path (:web-path-prefix cfg)
        cur-handler (.getHandler server)
        pattern-rule (RewritePatternRule. "/*" "")
        rewrite-hander (doto (RewriteHandler.) (.addRule pattern-rule) (.setHandler cur-handler))
        context-handler (doto (ContextHandler. base-path) (.setHandler rewrite-hander))
        context-handler-coll (ContextHandlerCollection. (into-array ContextHandler [context-handler]))]
    (.setHandler server context-handler-coll)
    server))

(defn configure-start-bind-jetty
  "Configures, starts and binds jetty server instance to server.runtime/jetty-server."
  []
  (let [ring-handler (ns-resolve 'server.handler.core 'ring-handler)
        host (or (System/getProperty "jetty-host") const/DEFAULT_JETTY_HOST)
        port (if-let [port (System/getProperty "jetty-port")]
               (Integer. port) *jetty-port*)
        _ (log/info "Starting Jetty on host" host "port" port)
        server (ring.adapter.jetty/run-jetty
                ring-handler {:host host
                              :port port
                              :configurator (partial configurator rt/cfg)
                              :join? false})]

    (log/info "Jetty started")
    (rt/bind-jetty-server server)))

(defn -main
  "Bootstraps processing node."
  [& _]
  ;; NOTE: we require next two ns dynamically to avoid problem with java 'Method code
  ;; too large' which happens when you require these ns in a normal way and AOT compile
  ;; starter class
  (log-env-and-properties)
  (require 'server.servlet)
  (require 'server.handler.core)
  (let [servlet-init (ns-resolve 'server.servlet 'init)]
    (servlet-init)
    (configure-start-bind-jetty)
    (.join rt/jetty-server)))
