(ns com.adaiasmagdiel.raytracer.hittable 
  (:require
    [com.adaiasmagdiel.raytracer.vec3 :as vec3]))

(defn hit-record [t p normal front-face]
  {:t t
   :point p
   :normal normal
   :front-face front-face})

(defn set-face-normal [hr ray outward-normal]
  (let [front-face (< (vec3/dot (:direction ray) outward-normal) 0)
        normal (if front-face outward-normal (vec3/scalar-mul outward-normal -1))]
    (hit-record (:t hr) (:point hr) normal front-face)))

(defn hit-world
  [world ray tmin tmax]
  (loop [objects world
         closest tmax
         hit-record nil]
    (if (empty? objects)
      hit-record
      (let [obj (first objects)
            hit ((:hit obj) obj ray tmin closest)]
        (if hit
          (recur (rest objects) (:t hit) hit)
          (recur (rest objects) closest hit-record))))))
