(ns server.buildings.v1.api
  (:require [ring.util.response :as rr]
            [server.buildings.v1.api :as v1.api]
            [server.buildings.v1.db :as db.buildings]))

(defn list-buildings
  [ctx _]
  (let [ds (:pg-ds ctx)
        buildings (db.buildings/list-buildings ds)]
    (-> (rr/response buildings)
        (rr/status 200))))
