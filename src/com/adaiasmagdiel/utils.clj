(ns com.adaiasmagdiel.utils)

(defn random-double [min max]
  (+ min (* (- max min) (rand))))

(defn clamp [x min max]
  (cond
    (< x min) min
    (> x max) max
    :else x))
