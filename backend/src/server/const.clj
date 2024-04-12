(ns server.const)

(def CFG_FILE_NAME
  "Location of the cfg file on file system."
  "/etc/bmstu/config.edn")

(def DEFAULT_QUERY_LIMIT 25)

(def DEFAULT_NREPL_HOST "0.0.0.0")
(def DEFAULT_NREPL_PORT 7878)
(def DEFAULT_JETTY_HOST "0.0.0.0")
(def DEFAULT_JETTY_PORT 8080)

(def UTC_TIMEZONE_ID "UTC")

(def DEFAULT_CFG
  "Default cfg"
  {:main-db {:migrations-table "ClassroomBooking.schema_migrations"
             :migration-dir "migrations"

             :connection-timeout 10000
             :auto-commit true
             :validation-timeout 5000
             :idle-timeout 600000
             :max-lifetime 1800000
             :minimum-idle 10
             :maximum-pool-size 10
             :pool-name "main_pool"
             :adapter "pgjdbc-ng"
             :username "postgres"
             :password "postgres"
             :database-name "main_db"
             :server-name "127.0.0.1"
             :port-number 5432
             :register-mbeans false
             :jdbc-url "jdbc:pgsql://127.0.0.1:5432/main_db"
             :leak-detection-threshold 30000}

   :jwt-secret "secret"

   :enabled-credential-verifiers #{:cas :default}})
