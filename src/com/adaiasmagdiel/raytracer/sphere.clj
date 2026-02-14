(ns com.adaiasmagdiel.raytracer.sphere
  (:require [com.adaiasmagdiel.raytracer.hittable :as h]
            [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]
            [clojure.math :as math])
  (:import [com.adaiasmagdiel.raytracer.ray Ray]
           [com.adaiasmagdiel.raytracer.vec3 Vec3]
           [com.adaiasmagdiel.raytracer.hittable HitRecord IHittable]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(deftype Sphere
  [^Vec3 center
   ^double radius
   material]

  IHittable

  (hit [this ray tmin tmax]
       (let [^Ray ray ray
             ^double tmin tmin
             ^double tmax tmax
             ^Vec3 origin (.origin ray)
             ^Vec3 direction (.direction ray)

             oc (vec3/sub center origin)

             a (vec3/length-sq direction)
             h (vec3/dot direction oc)
             c (- (vec3/length-sq oc) (* radius radius))

             discriminant (- (* h h) (* a c))]

         (when (>= discriminant 0.0)
           (let [sqrtd (math/sqrt discriminant)

                 root1 (/ (- h sqrtd) a)
                 root2 (/ (+ h sqrtd) a)

                 t (cond
                     (and (> root1 tmin) (< root1 tmax)) root1
                     (and (> root2 tmin) (< root2 tmax)) root2
                     :else nil)]

             (when t
               (let [p (ray/at ray t)
                     outward-normal (vec3/unit (vec3/sub p center))
                     hr (HitRecord. t p outward-normal false material)]
                 (h/set-face-normal hr ray outward-normal))))))))

(defn create [center radius material]
  (Sphere. center (double radius) material))
