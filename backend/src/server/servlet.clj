(ns server.servlet
  "Server startup"
  (:require [buddy.hashers]
            [clojure.core.memoize]
            [clojure.data]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [nrepl.server :as nrepl-server]
            [server.const :as const]
            [server.handler.core :as handler]
            [server.pg :as pg]
            [server.migrations :as migrations]
            [server.runtime :as rt]
            [server.events.v1.api]
            [server.users.v1.api]
            [server.util :as u]
            [slingshot.slingshot :refer [throw+]]))

(defn load-cfg
  "Reads configuration data in the edn format from the specified `cfg-file-name`
  file. Returns a configuration object for success and nil otherwise."
  [cfg-file-name]
  (log/infof "Loading app cfg from %s" cfg-file-name)
  (let [cfg-exists? (.exists (io/file cfg-file-name))
        cfg (if cfg-exists?
              (clojure.edn/read-string (slurp cfg-file-name))
              (throw+ {:error :config-is-missing
                       :message (format "Configuration loading error: %s is missing"
                                        (or cfg-file-name "NO_CFG_FILE_NAME"))}))
        cfg (merge const/DEFAULT_CFG cfg)]
    cfg))

(s/fdef make-ring-handler
  :args (s/cat :opts (s/keys :req-un [::cfg
                                      ::pg-ds
                                      ::dev-mode?]))
  :ret any?)

(defn make-ring-handler
  "Makes base handler"
  [{:keys [cfg
           pg-ds
           dev-mode?]}]
  (let [base-handler (handler/make-base-handler {:cfg cfg
                                                 :pg-ds pg-ds})]

    (if dev-mode?
      base-handler
      (handler/wrap-uberwar base-handler))))

(defn nrepl-handler
  "Handler for nRepl"
  []
  (require 'cider.nrepl)
  (ns-resolve 'cider.nrepl 'cider-nrepl-handler))

(defn- shutdown
  "Shutdown the system stopping all started services and closing opened connections. NOTE:
   current implementation is naive and does not wait for all tasks completion."
  []
  (log/info "Shutdown signal received, terminating.")
  (reset! rt/shutting-down? true)

  (when (bound? #'rt/jetty-server)
    (log/info "Stopping Jetty server")
    (.stop rt/jetty-server))

  (when (bound? #'rt/nrepl-server)
    (log/info "Stopping nrepl server")
    (nrepl-server/stop-server rt/nrepl-server))

  (log/info "Shutdown complete.")
  (log/info "*** THREAD STATS AFTER SHUTDOWN ***\n" (u/get-thread-stats true)))

(defn- add-shutdown-hook
  []
  (.. Runtime
      getRuntime
      (addShutdownHook
       (Thread.
        (fn [] (shutdown))
        "shutdown-watcher")))
  (log/trace "Added shutdown hook."))

(defn- init-nrepl
  []
  (when (= "true" (System/getProperty "start-nrepl"))
    (let [host (or (System/getProperty "nrepl-host") const/DEFAULT_NREPL_HOST)
          port (if-let [port (System/getProperty "nrepl-port")]
                 (Integer. port)
                 const/DEFAULT_NREPL_PORT)
          _ (log/info "Starting nrepl server on host" host "port" port)
          nrepl-server (nrepl-server/start-server :bind host
                                                  :port port
                                                  :handler (nrepl-handler))]
      (rt/bind-nrepl-server nrepl-server)
      (log/info (str "Started nrepl server on " (str (:server-socket nrepl-server)))))))

(defn init
  "CALLED once on servlet startup. "
  [& {:keys [dev-mode?] :or {dev-mode? false}}]
  (log/trace "=> init")

  (reset! rt/starting-up? true)
  (init-nrepl)

  (let [cfg (load-cfg const/CFG_FILE_NAME)
        pg-ds (pg/connect! cfg)]
    (rt/bind-cfg cfg)
    (rt/bind-pg-ds pg-ds)
    (migrations/migrate-db cfg)
    (rt/bind-cfg cfg)
    (handler/bind-ring-handler (make-ring-handler {:cfg cfg
                                                   :pg-ds rt/pg-ds
                                                   :dev-mode? dev-mode?}))
    (reset! rt/starting-up? false)
    (add-shutdown-hook)
    (log/trace "*** THREAD STATS AFTER START ***\n" (u/get-thread-stats true))
    (log/trace "<= init")))

(u/instrument-ns *ns*)
