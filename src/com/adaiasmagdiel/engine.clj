(ns com.adaiasmagdiel.engine
	(:require [com.adaiasmagdiel.settings :as s]))

(defn compute-pixel-color [x y]
	(let [rr (double (/ x (- s/WIDTH 1)))
							gg (double (/ y (- s/HEIGHT 1)))
							bb 0

							r (int (* 255.999 rr))
							g (int (* 255.999 gg))
							b (int (* 255.999 bb))]

		(vector r g b)))
