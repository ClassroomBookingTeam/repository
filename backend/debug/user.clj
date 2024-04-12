(ns user
  "Contains functions designed to be called from REPL during development."
  (:require [server.runtime :as rt]))

(defn pg-ds
  []
  rt/pg-ds)
