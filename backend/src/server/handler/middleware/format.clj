(ns server.handler.middleware.format
  (:require [jsonista.core :as j]
            [jsonista.tagged :as jt]
            [muuntaja.core]
            [muuntaja.format.core]
            [muuntaja.format.edn]
            [muuntaja.format.json]
            [server.time :as time]
            [server.util :as u]))

(def mapper
  "json->edn jsonista object mapper for tagged json conversion. Works normally for json
  stings without tags."
  (j/object-mapper
   {:encode-key-fn true
    :decode-key-fn u/memoized->kebab-case-keyword
    :modules [(jt/module
               {:handlers {clojure.lang.Keyword {:tag "!kw"
                                                 :encode jt/encode-keyword
                                                 :decode keyword}
                           clojure.lang.PersistentHashSet {:tag "!set"
                                                           :encode jt/encode-collection
                                                           :decode set}
                           java.util.UUID {:tag "!uuid"
                                           :encode jt/encode-str
                                           :decode #(java.util.UUID/fromString %)}
                           java.util.Date {:tag "!inst"
                                           :encode jt/encode-str
                                           :decode time/str->inst}
                           java.time.ZonedDateTime {:tag "!zdt"
                                                    :encode jt/encode-str
                                                    :decode time/str->inst}}})]}))

(def default-options
  "Options that are used if not specified by client in HTTP request."
  {:http {:extract-content-type muuntaja.core/extract-content-type-ring
          :extract-accept-charset muuntaja.core/extract-accept-charset-ring
          :extract-accept muuntaja.core/extract-accept-ring
          :decode-request-body? (constantly true)
          :encode-response-body? muuntaja.core/encode-collections}
   :allow-empty-input? true
   :return :input-stream
   :default-charset "utf-8"
   :charsets #{"utf-8"}
   :default-format "application/json"
   :formats {"application/json" (muuntaja.format.core/map->Format
                                 {:name "application/json"
                                  :encoder-opts {:encode-key-fn (comp u/memoized->snake_case
                                                                      name)}
                                  :decoder-opts {:decode-key-fn u/memoized->kebab-case-keyword}
                                  :decoder [muuntaja.format.json/decoder
                                            {:mapper mapper}]
                                  :encoder [muuntaja.format.json/encoder]})
             "application/edn" muuntaja.format.edn/format}})

(defn- set-params
  "Returns a request that consists of the rest of the `:body-params` conjucted onto
   `:params` key of the specified `request`. If content-type is multipart/form-data
   then we expect params under :edn-params key."
  [request]
  (let [params (reduce-kv (fn [acc k v]
                            (assoc acc (u/memoized->kebab-case-keyword (name k)) v))
                          {}
                          (:params request))
        body-params (:body-params request)
        request (assoc request :params params)]
    (cond
      (not (map? body-params)) request
      (empty? body-params) request
      (empty? params) (assoc request :params body-params)
      :else (update request :params merge body-params))))

(defn wrap-format
  "Returns a `handler` wrapped with a middleware that is applied to response and request.

   For a request negotiates a body based on accept, accept-charset and content-type headers
   and decodes the `:body` into `:body-params` and merges them onto `:params`.

   For the response encodes `:body` according to the negotiation information or override
   information provided by the handler."
  [handler]
  (let [formatter (muuntaja.core/create default-options)]
    (fn request-response-format-fn
      [request]
      (let [request (set-params
                     (muuntaja.core/negotiate-and-format-request formatter request))
            response (handler request)]
        (u/with-redefine-methods print-method [nil
                                               (fn [_ writer]
                                                 (.write writer "nil-value"))

                                               clojure.lang.AFunction
                                               (fn [_ w]
                                                 (.write w "fn"))]
          (muuntaja.core/format-response formatter request response))))))
