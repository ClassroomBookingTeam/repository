(ns server.util
  (:require [camel-snake-kebab.core :as csk]
            [clojure.core.memoize :as m]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [orchestra.spec.test :as st]
            [server.runtime :as rt]))

(def memoized->snake_case
  (m/fifo csk/->snake_case {} :fifo/threshold 512))

(def memoized->kebab-case-keyword
  (m/fifo csk/->kebab-case-keyword {} :fifo/threshold 512))

(s/fdef str->bool
  :args (s/cat :s (s/nilable string?))
  :ret boolean?)

(defn str->bool
  "Returns boolean representing the value true if the string argument
        is not null and is equal, ignoring case, to the string \"true\".
        Otherwise, returns boolean representing the value false."
  [s]
  (boolean (Boolean. s)))

(defn ms->minutes
  "Converts milliseconds to minutes."
  [ms-amount]
  (let [ms-in-sec 1000
        secs-in-min 60]
    (/ (/ ms-amount ms-in-sec) secs-in-min)))

(defn instrument-fns
  "Instruments all function specs if in dev mode defined by `syms`."
  [syms]
  (when (str->bool (rt/env :dev))
    (st/instrument syms)))

(defn instrument-ns
  "Instruments all function specs if in dev mode for particular `ns`."
  [ns]
  (let [syms (stest/enumerate-namespace [(ns-name ns)])]
    (instrument-fns syms)))

(defn instrument-fn
  "Instruments function specs if in dev mode."
  [sym]
  (instrument-fns [sym]))

(defmacro with-redefine-methods
  "Redefines methods of `mutlifn` defined in `redefines` and executes `body` in
        new context.
        Sample usage:
        (with-redefine-methods print-method [clojure.lang.AFunction
                                             (fn [x writer]
                                               (.write writer \"fun\"))

                                             nil
                                             (fn [x writer]
                                               (.write writer \"nil-value\"))]
          (print-method (fn []) *out*)
          (print-method nil *out*)
          (print-method 1 *out*))"
  [multifn redefines & body]
  (let [redefines (->> redefines
                       (partition-all 2)
                       (map (fn [[dispatch-val f]]
                              {:binding (gensym)
                               :dispatch-val dispatch-val
                               :f f})))
        bindings (reduce (fn [acc {:keys [binding dispatch-val]}]
                           (conj acc binding `(get (methods ~multifn) ~dispatch-val)))
                         []
                         redefines)
        new-methods (map (fn [{:keys [dispatch-val f]}]
                           `(. ~multifn clojure.core/addMethod ~dispatch-val ~f))
                         redefines)
        finals (map (fn [{:keys [binding dispatch-val]}]
                      `(if ~binding
                         (. ~multifn clojure.core/addMethod ~dispatch-val ~binding)
                         (remove-method ~multifn ~dispatch-val)))
                    redefines)]
    `(let ~bindings
       (try
         ~@new-methods
         ~@body
         (finally
           ~@finals)))))

(s/fdef conform-request-params
  :args (s/cat :params (s/keys :req-un [::spec
                                        ::request]))
  :ret (s/keys :req-un [::errors
                        ::request]))

(defn conform-request-params
  "Conforms `request` :params against `spec`, return updated request."
  [{:keys [spec request]}]
  (let [conformed-request (update request :params #(s/conform spec %))
        conformed-params (:params conformed-request)
        errors (cond
                 (map? conformed-params)
                 (reduce-kv (fn [acc k v]
                              (cond-> acc
                                (map? v)
                                (conj {:path (memoized->snake_case (name k))
                                       :message (:msg v)})

                                (sequential? v)
                                (conj {:k (memoized->snake_case (name k))
                                       :message (->> v
                                                     (filter map?)
                                                     first
                                                     :msg)})))
                            []
                            conformed-params)

                 (= :clojure.spec.alpha/invalid
                    conformed-params)
                 [{:message "Чего-то не хватает"
                   :path "body"}]

                 :else
                 [{:message "Неизвестная ошибка"
                   :path "body"}])]
    {:request conformed-request
     :errors errors}))


(defn get-thread-stats
  "Returns a string with running thread stats (count all, count daemon, cont
    non-daemon). If `verbose?` is true, adds names of all threads."
  [verbose?]
  (let [thread-name (fn [thread]
                      (let [group-name (or (some-> thread
                                                   (.getThreadGroup)
                                                   (.getName))
                                           "[NO_THREAD_GROUP_NAME]")
                            thread-name (or (some-> thread
                                                    (.getName))
                                            "[NO_THREAD_NAME]")]
                        (str group-name "/" thread-name)))
        running-threads (->> (.keySet (Thread/getAllStackTraces))
                             (sort-by thread-name))
        count-all (count running-threads)
        count-daemon (->> running-threads
                          (filter #(.isDaemon %))
                          (count))
        count-non-daemon (->> running-threads
                              (filter #(false? (.isDaemon %)))
                              (count))
        stats (format "Thread stats. Total : %d, Daemon :%d, Non-daemon : %d"
                      count-all count-daemon count-non-daemon)
        stats (if verbose?
                (->> running-threads
                     (reduce (fn [acc thread]
                               (str acc (format "\nThread : %s, daemon? : %s"
                                                (thread-name thread)
                                                (.isDaemon thread))))
                             stats))
                stats)]
    stats))

(defn add-ns
  [coll ns]
  (reduce-kv (fn [acc k v]
               (let [new-k (keyword ns (name k))]
                 (-> acc
                     (assoc new-k v)
                     (dissoc k))))
             {}
             coll))
