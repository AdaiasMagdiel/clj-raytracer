(ns com.adaiasmagdiel.utils
  (:require [clojure.string :as str]))

(def MAX_COLS 50)

(defn log-progress [remaining total]
  (binding [*out* *err*]
    (let [done (- total remaining)
          percent (double (* (/ done total) 100))
          filled (int (* (/ percent 100) MAX_COLS))
          empty (- MAX_COLS filled)
          bar (str "[" (str/join "" (repeat filled "#"))
                   (str/join "" (repeat empty ".")) "]")]
      (print (format "\r%s %.1f%% complete  " bar percent))
      (flush))))

(defn random-double [min max]
  (+ min (* (- max min) (rand))))

(defn clamp [x min max]
  (cond
    (< x min) min
    (> x max) max
    :else x))
