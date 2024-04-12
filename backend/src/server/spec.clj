(ns server.spec
  (:require [clojure.spec.alpha :as s]
            [server.time :as time]
            [superstring.core :as string]))

;; macro

(defmacro with-conformer
  [{:keys [bind msg]} & body]
  `(s/conformer
    (fn [~bind]
      (try
        ~@body
        (catch Exception e#
          {:msg (if ~msg
                  ~msg
                  "Неизвестная ошибка")})))))

;; conformers

(s/def ::->email
  (with-conformer {:bind value
                   :msg "Некорректное значение"}
   (if (and (not (string/blank? value))
            (re-matches #"(.+?)@(.+?)\.(.+?)" value))
     value
     {:msg "Неккоректное значение"})))

(s/def ::->int
  (with-conformer {:bind value
                   :msg "Некорректное значение"}
    (cond
      (and (number? value)
           (>= value 0))
      value

      :else
      (let [parsed (Integer/parseInt value)]
        (if (>= parsed 0)
          parsed
          {:msg "Отрицательные значения недопустимы"})))))

(s/def ::->pos-int
  (with-conformer {:bind value
                   :msg "Некорректное значение"}
    (cond
      (and (number? value)
           (pos? value))
      value

      :else
      (let [parsed (Integer/parseInt value)]
        (if (pos? parsed)
          parsed
          {:msg "Отрицательные и нулевые значения недопустимы"})))))

(s/def ::->uuid
  (with-conformer {:bind value
                   :msg "Некорректное UUID значение"}
    (cond
      (uuid? value) value

      :else (java.util.UUID/fromString value))))

(s/def ::->inst
  (with-conformer {:bind value
                   :msg "Некорректное значение даты"}
    (cond
      (inst? value) value

      :else (time/str->inst value))))

;; common specs

(s/def ::id ::->uuid)
(s/def ::page ::->pos-int)
(s/def ::page-size ::->pos-int)
(s/def ::available-at ::->inst)
(s/def ::email ::->email)
