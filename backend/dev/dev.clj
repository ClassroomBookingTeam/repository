(ns dev
  (:require [server.core]
            [server.runtime :as rt]
            [server.servlet :as servlet]
            [server.tagged-literals]))


(defn start!
  "Initialise servlet, starts backend server and bind it"
  []
  (when-not (bound? #'rt/cfg)
    (servlet/init :dev-mode? true))
  (if (bound? #'rt/jetty-server)
    (.start rt/jetty-server)
    (binding [server.core/*jetty-port* 9501]
      (server.core/configure-start-bind-jetty)))
  :started)

(defn stop!
  "Stops backend server instance"
  []
  (when (bound? #'rt/jetty-server)
    (.stop rt/jetty-server))
  :stopped)

(defn restart!
  "Restarts backend web server"
  []
  (stop!)
  (start!)
  :restarted)
