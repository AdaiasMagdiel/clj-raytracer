(ns com.adaiasmagdiel.raytracer.material
  (:require [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]
            [clojure.math :as math])
  (:import [com.adaiasmagdiel.raytracer.ray Ray]
           [com.adaiasmagdiel.raytracer.vec3 Vec3]
           [com.adaiasmagdiel.raytracer.hittable HitRecord]
           [java.util.concurrent ThreadLocalRandom]))

(defprotocol Material
  (scatter [this ray-in hit-record]))

(defrecord Lambertian [albedo]
  Material
  (scatter [this ray-in hit-record]
    (let [^HitRecord hr hit-record
          normal (.normal hr)
          point  (.point hr)
          
          scatter-direction (vec3/add normal
                                      (vec3/random-unit-vector))
          direction (if (vec3/near-zero? scatter-direction)
                      normal
                      scatter-direction)
          scattered (ray/create point direction)]
      {:scattered   scattered
       :attenuation albedo
       :hit?        true})))

(defrecord Metal [albedo fuzz]
  Material
  (scatter [this ray-in hit-record]
    (let [^HitRecord hr hit-record
          normal (.normal hr)
          point  (.point hr)
          
          ^Ray r ray-in
          actual-fuzz (if (< fuzz 1.0) fuzz 1.0)

          ^Vec3 in-dir (.direction r)
          unit-dir     (vec3/unit in-dir)
          reflected    (vec3/reflect unit-dir normal)

          fuzzed-dir   (vec3/add
                        (vec3/unit reflected)
                        (vec3/scalar-mul (vec3/random-unit-vector) actual-fuzz))

          scattered-ray (ray/create point fuzzed-dir)
          ^Vec3 scattered-dir (.direction scattered-ray)]

      (when (> (vec3/dot scattered-dir normal) 0)
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
    (let [^HitRecord hr hit-record
         normal (.normal hr)
         point  (.point hr)
         front-face (.front-face hr)
         
         ^Ray r ray-in
         attenuation (vec3/create 1.0 1.0 1.0)
         
         ri (if front-face
              (/ 1.0 refraction-index)
              refraction-index)
         
         ^Vec3 in-dir (.direction r)
         unit-dir     (vec3/unit in-dir)
         
         cos-theta (min (vec3/dot (vec3/scalar-mul unit-dir -1.0) normal) 1.0)
         sin-theta (math/sqrt (- 1.0 (* cos-theta cos-theta)))
         
         rng (ThreadLocalRandom/current)
         cannot-refract (> (* ri sin-theta) 1.0)
         
         direction (if (or cannot-refract
                           (> (reflectance cos-theta ri)
                              (.nextDouble rng)))
                     (vec3/reflect unit-dir normal)
                     (vec3/refract unit-dir normal ri))]

    {:scattered   (ray/create point direction)
     :attenuation attenuation
     :hit?        true})))
