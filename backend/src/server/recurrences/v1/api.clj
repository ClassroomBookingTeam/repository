(ns server.recurrences.v1.api
  (:require [ring.util.response :as rr]
            [server.recurrences.v1.api :as v1.api]
            [server.recurrences.v1.db :as db.recurrences]))

(defn list-recurrences
  [ctx _]
  (let [ds (:pg-ds ctx)
        recurrences (db.recurrences/list-recurrences ds)]
    (-> (rr/response recurrences)
        (rr/status 200))))
