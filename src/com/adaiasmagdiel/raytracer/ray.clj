(ns com.adaiasmagdiel.raytracer.ray
	(:require [com.adaiasmagdiel.raytracer.vec3 :as vec3]))

(defn create [origin direction]
	{:origin origin
		:direction direction})

(defn at [ray t]
	(vec3/add (:origin ray)
 	(vec3/scalar-mul (:direction ray) t)))

