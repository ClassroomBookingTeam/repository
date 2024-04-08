(defproject server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]

                 [criterium "0.4.6"]
                 [clj-http "3.12.3"]
                 [cheshire "5.10.2"]
                 [orchestra "2021.01.01-1"]
                 [slingshot "0.12.2"]
                 [org.clojure/test.check "1.1.1"]

                 [org.apache.logging.log4j/log4j-slf4j-impl "2.19.0"]

                 [ring/ring-core "1.9.5"]
                 [ring/ring-headers "0.3.0"]
                 [ring-cors "0.1.13"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.6.2"]
                 [metosin/muuntaja "0.6.8"]

                 ;; DB deps
                 [hikari-cp "3.0.1" :exclusions [org.slf4j/slf4j-api]]
                 [org.postgresql/postgresql "42.3.3"]
                 [migratus/migratus "1.3.5"]
                 [com.github.seancorfield/next.jdbc "1.2.659"]
                 [com.github.seancorfield/honeysql "2.5.1103"]
                 [com.impossibl.pgjdbc-ng/pgjdbc-ng "0.8.9"]
                 [nilenso/honeysql-postgres "0.4.112"]

                 [com.cognitect/transit-clj "1.0.324"]
                 [metosin/jsonista "0.3.4"]

                 [buddy/buddy-sign "3.4.333"]
                 [buddy/buddy-hashers "1.8.158"]

                 [protojure "1.7.3"
                  :exclusions [org.eclipse.jetty.http2/http2-client
                               org.eclipse.jetty/jetty-alpn-java-client
                               org.slf4j/slf4j-api
                               clj-time]]

                 [org.eclipse.jetty/jetty-server "9.4.40.v20210413"]
                 [org.eclipse.jetty/jetty-rewrite "9.4.40.v20210413"]
                 [ring/ring-jetty-adapter "1.9.5"
                  :exclusions [org.eclipse.jetty/jetty-server]]

                 [org.jboss.logmanager/jboss-logmanager "2.1.10.Final"]

                 [camel-snake-kebab "0.4.2"]
                 [superstring "3.1.0"]
                 [dev.weavejester/medley "1.5.0"]

                 [cider/cider-nrepl "0.28.5"]

                 ;;conflict resolvation
                 [commons-io "2.11.0"]
                 [nrepl "1.0.0"]]
  :repl-options {:init-ns server.core}
  :source-paths ["src" "dev" "debug"]
  :clean-targets ^{:protect false} ["target"]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "1.3.0"]
                                  [binaryage/devtools "1.0.6"]
                                  [ring/ring-devel "1.8.1"]
                                  [ring/ring-mock "0.4.0"]]

                   :repositories [["jitpack" "https://jitpack.io"]]
                   :resource-paths ["target" "test/resources"]
                   :jvm-opts ["-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
                              "-Dlog4j2.configurationFile=/var/log/bmstu/server-log4j2-dev.xml"
                              "-Ddev=true"
                              "-Dpayload-logger=false"
                              "-XX:-OmitStackTraceInFastThrow"
                              "-Djetty-port=9501"
                              "-Dclojure.spec.check-asserts=false"]
                   :plugins [[lein-shell "0.5.0"]]}

             :dev-nrepl [:dev
                         {:jvm-opts ["-Dstart-nrepl=true"]}]

             :uberjar {:main server.core
                       :aot [server.core]
                       :plugins [[lein-nvd "1.4.1"]]}}

  :aliases
  {"dev"
   ["do"
    ["with-profile" "dev" "trampoline" "run" "-m" "server.core"]]

   "verify"
   ["with-profile" "uberjar"
    "nvd" "check"]

   "dev-nrepl"
   ["do"
    ["with-profile" "dev-nrepl" "trampoline" "run" "-m" "server.core"]]

   "debug-nrepl"
   ["do"
    ["with-profile" "debug-nrepl" "trampoline" "run" "-m" "server.core"]]})
