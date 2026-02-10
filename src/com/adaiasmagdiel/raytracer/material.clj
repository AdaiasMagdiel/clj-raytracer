(ns com.adaiasmagdiel.raytracer.material
  (:require [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]
            [clojure.math :as math]))

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

(defn- reflectance [cosine refraction-index]
  (let [r0_ (/ (- 1 refraction-index) (+ 1 refraction-index))
        r0 (* r0_ r0_)]
    (+ r0 (* (- 1 r0) (math/pow (- 1 cosine) 5)))))

(defrecord Dielectric [refraction-index]
  Material
  (scatter [this ray-in hit-record]
    (let [attenuation [1.0 1.0 1.0]
          normal      (:normal hit-record)
          ri          (if (:front-face hit-record)
                        (/ 1.0 refraction-index)
                        refraction-index)
          unit-dir    (vec3/unit (:direction ray-in))
          cos-theta (min (vec3/dot (vec3/scalar-mul unit-dir -1) normal) 1.0)
          sin-theta (math/sqrt (- 1.0 (* cos-theta cos-theta)))
          
          cannot_refract  (> (* ri sin-theta) 1.0)
          direction      (if (or
                              cannot_refract
                              (> (reflectance cos-theta ri) (rand)))
                           (vec3/reflect unit-dir normal)
                           (vec3/refract unit-dir normal ri))]

      {:scattered   (ray/create (:point hit-record) direction)
       :attenuation attenuation
       :hit?        true})))
