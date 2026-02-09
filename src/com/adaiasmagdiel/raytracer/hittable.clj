(ns com.adaiasmagdiel.raytracer.hittable 
  (:require
    [com.adaiasmagdiel.raytracer.vec3 :as vec3]))

(defn hit-record [p normal t front-face]
  {:t t
   :point p
   :normal normal
   :front-face front-face})

(defn set-face-normal [hr ray outward-normal]
  (let [front-face (< (vec3/dot (:direction ray) outward-normal) 0)
        normal (if front-face outward-normal (vec3/scalar-mul outward-normal -1))]
    (hit-record (:t hr) (:point hr) normal front-face)))
