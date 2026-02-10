(ns com.adaiasmagdiel.raytracer.sphere
  (:require [com.adaiasmagdiel.raytracer.hittable :as h]
            [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]
            [clojure.math :as math]))

(defn hit
  [sphere ray tmin tmax]
  (let [center (:center sphere)
        radius (:radius sphere)
        origin (:origin ray)
        direction (:direction ray)

        oc (vec3/sub center origin)

        a (vec3/length-sq direction)
        h (vec3/dot direction oc)
        c (- (vec3/length-sq oc) (* radius radius))

        discriminant (- (* h h) (* a c))]

    (when (>= discriminant 0)
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
                hr (h/hit-record t p outward-normal false (:material sphere))]
            (h/set-face-normal hr ray outward-normal)))))))

(defn create [center radius material]
  {:type :sphere
   :center center
   :radius radius
   :material material
   :hit hit})
