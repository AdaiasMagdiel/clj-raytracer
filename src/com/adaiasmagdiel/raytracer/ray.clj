(ns com.adaiasmagdiel.raytracer.ray
  (:require [com.adaiasmagdiel.raytracer.vec3 :as vec3])
  (:import [com.adaiasmagdiel.raytracer.vec3 Vec3]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(defrecord Ray [^Vec3 origin ^Vec3 direction])

(defn create
  "Creates a high-performance Ray record."
  ^Ray [^Vec3 origin ^Vec3 direction]
  (->Ray origin direction))

(defn at
  "Calculates point at distance t: P(t) = A + tB"
  ^Vec3 [^Ray ray ^double t]
  (let [orig (.origin ray)
        dir  (.direction ray)]
    (vec3/add orig (vec3/scalar-mul dir t))))
