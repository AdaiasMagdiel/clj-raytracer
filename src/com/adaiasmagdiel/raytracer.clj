(ns com.adaiasmagdiel.raytracer
	(:gen-class)
	(:require [quil.core :as q]))

(def WIDTH 640)
(def HEIGHT 480)

(defn setup []
	(let [img (q/create-image WIDTH HEIGHT :rgb)]
		
		(dotimes [y HEIGHT]
			(dotimes [x WIDTH]
				(let [rr (double (/ x (- WIDTH 1)))
										gg (double (/ y (- HEIGHT 1)))
										bb 0

										r (int (* 255.999 rr))
										g (int (* 255.999 gg))
										b (int (* 255.999 bb))]
					
					(q/set-pixel img x y (q/color r g b)))))

		(q/image img 0 0)))

(defn -main
	[& args]
	(q/defsketch raytracer
  :title "Raytracer"
  :settings #(q/smooth 2)
  :setup setup
  :size [WIDTH HEIGHT]))
