(ns com.adaiasmagdiel.raytracer.material
  (:require [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]))

(defprotocol Material
  (scatter [this ray-in hit-record]
    "Returns a map containing the scattered ray and attenuation color, or nil if no scatter occurs."))

(defrecord Lambertian [albedo]
  Material
  (scatter [this ray-in hit-record]
    (let [scatter-direction (vec3/add (:normal hit-record) (vec3/random-unit-vector))
          
          direction (if (vec3/near-zero? scatter-direction)
                      (:normal hit-record)
                      scatter-direction)
          
          scattered (ray/create (:point hit-record) direction)]
      
      {:scattered   scattered
       :attenuation albedo
       :hit?        true})))

(defrecord Metal [albedo fuzz]
  Material
  (scatter [this ray-in hit-record]
    (let [actual-fuzz (if (< fuzz 1.0) fuzz 1.0)

          unit-dir    (vec3/unit (:direction ray-in))
          reflected   (vec3/reflect unit-dir (:normal hit-record))

          fuzzed-dir  (vec3/add
                       (vec3/unit reflected)
                       (vec3/scalar-mul (vec3/random-unit-vector) actual-fuzz))

          scattered-ray (ray/create (:point hit-record) fuzzed-dir)]

      (when (> (vec3/dot (:direction scattered-ray) (:normal hit-record)) 0)
        {:scattered   scattered-ray
         :attenuation albedo
         :hit?        true}))))
