(ns server.handler.middleware.ctx
  "Context injector. Used to inject common staff during request processing."
  (:require [clojure.spec.alpha :as s]
            [server.auth.authentication-cookie]
            [server.runtime :as rt]
            [server.util :as u]))

(defn extract-ctx
  "Extracts context from `request`. Assumes that request has been process by standard
   ring middleware to extract params and perform content type conversion."
  [{:keys [body-params]}]
  (:context body-params))

(s/fdef make-ctx
  :args (s/cat :opts (s/keys :req-un [::cfg])
               :request map?)
  :ret (s/keys :req-un [::cfg
                        ::pg-ds
                        ::auth-user-id]))
(defn make-ctx
  "Creates an context map. Must be called per ring request after it was processed by
   standards middleware to convert content type, query and body params."
  [{:keys [cfg]} request]
  (let [ctx (extract-ctx request)]
    {:ctx ctx
     :cfg cfg
     :auth-user-id (:auth-user-id (:auth-info request))
     :pg-ds rt/pg-ds}))

;;
;; Ctx Wrappers
;;

(s/fdef wrap-ctx
  :args (s/cat :handler fn?
               :opts (s/keys :req-un [::cfg]))
  :ret fn?)

(defn wrap-ctx
  "Binds per thread value of `rt/*ctx*` and calls handler in the bound context.
   Assumes that standard ring middleware for parsing params and converting content
   type has been run.
   Params:
    `handler` - handler to wrap.
    `opts` - see spec.
   Returns anonymous function which can be used in middleware."
  [handler opts]
  (fn bind-ctx-handler [request]
    (let [ctx (make-ctx opts request)]
      (binding [rt/*ctx* ctx]
        (handler request)))))

(u/instrument-ns *ns*)
