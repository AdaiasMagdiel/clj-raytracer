(ns com.adaiasmagdiel.image
  (:require [clojure.string :as str]
            [com.adaiasmagdiel.settings :as s]
            [com.adaiasmagdiel.engine :as engine]))

(def MAX_COLS 50)

(defn log-progress [remaining total]
  (binding [*out* *err*]
    (let [done (- total remaining)
          percent (double (* (/ done total) 100))
          filled (int (* (/ percent 100) MAX_COLS))
          empty (- MAX_COLS filled)
          bar (str "[" (clojure.string/join "" (repeat filled "#")) 
                   (clojure.string/join "" (repeat empty ".")) "]")]
      (print (format "\r%s %.1f%% complete  " bar percent))
      (flush))))

(defn main []
	(println (str "P3\n" s/WIDTH " " s/HEIGHT "\n255"))

	(dotimes [y s/HEIGHT]
		(log-progress (- s/HEIGHT y 1) s/HEIGHT)

			(dotimes [x s/WIDTH]
				(let [[r g b] (engine/compute-pixel-color x y)]
					(println (str r " " g " " b))))))
