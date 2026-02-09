(ns com.adaiasmagdiel.image
  (:require [com.adaiasmagdiel.settings :as s]
            [com.adaiasmagdiel.engine :as engine]
            [com.adaiasmagdiel.utils :as utils]))



(defn main []
	(println (str "P3\n" s/WIDTH " " s/HEIGHT "\n255"))

	(dotimes [y s/HEIGHT]
		(utils/log-progress (- s/HEIGHT y 1) s/HEIGHT)

			(dotimes [x s/WIDTH]
				(let [[r g b] (engine/compute-pixel-color x y)]
					(println (str r " " g " " b))))))
