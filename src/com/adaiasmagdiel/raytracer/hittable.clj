(ns com.adaiasmagdiel.raytracer.hittable 
  (:require
    [com.adaiasmagdiel.raytracer.vec3 :as vec3])
  (:import [com.adaiasmagdiel.raytracer.ray Ray]
           [com.adaiasmagdiel.raytracer.vec3 Vec3]))

(deftype HitRecord
         [^double t
          ^Vec3 point
          ^Vec3 normal
          ^boolean front-face
          material])

(definterface IHittable
  (hit [ray tmin tmax]))

(defn set-face-normal
  [^HitRecord hr ^Ray ray outward-normal]
  (let [^Vec3 dir (.direction ray)
        front-face (< (vec3/dot dir outward-normal) 0.0)
        normal (if front-face
                 outward-normal
                 (vec3/scalar-mul outward-normal -1.0))]
    (HitRecord. (.t hr)
                (.point hr)
                normal
                front-face
                (.material hr))))

(defn hit-world
  [^objects world ^Ray ray ^double tmin ^double tmax]
  (loop [i 0
         closest tmax
         hit-record nil]
    (if (< i (alength world))
      (let [^IHittable obj (aget world i)
            hit (.hit obj ray tmin closest)]
        (if hit
          (recur (unchecked-inc i) (.t ^HitRecord hit) hit)
          (recur (unchecked-inc i) closest hit-record)))
      hit-record)))
