(ns com.adaiasmagdiel.engine
  (:require [com.adaiasmagdiel.settings :as s]
            [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]
            [com.adaiasmagdiel.raytracer.sphere :as sphere]
            [com.adaiasmagdiel.raytracer.hittable :as h]
            [com.adaiasmagdiel.utils :as utils]))

(def focal-length 1.0)
(def camera-center (vec3/create 0 0 0))
(def samples-per-pixel 10)
(def pixel-samples-scale (/ 1.0 samples-per-pixel))

(def viewport-u (vec3/create s/VIEWPORT_WIDTH 0 0))
(def viewport-v (vec3/create 0 (* -1 s/VIEWPORT_HEIGHT) 0))

(def pixel-delta-u (vec3/scalar-div viewport-u s/WIDTH))
(def pixel-delta-v (vec3/scalar-div viewport-v s/HEIGHT))

(def viewport-upper-left (vec3/sub
                          camera-center
                          (vec3/create 0 0 focal-length)
                          (vec3/scalar-div viewport-u 2)
                          (vec3/scalar-div viewport-v 2)))
(def pixel00-loc (vec3/add viewport-upper-left (vec3/scalar-mul (vec3/add pixel-delta-u pixel-delta-v) 0.5)))

(def world [(sphere/create (vec3/create 0 0 -1) 0.5)
            (sphere/create (vec3/create 0 -100.5 -1) 100)])

(defn paint-sky [r]
  (let [unity-direction (vec3/unit (:direction r))
        a (* 0.5 (+ 1.0 (vec3/y unity-direction)))]
    (vec3/add
     (vec3/scalar-mul (vec3/create 1.0 1.0 1.0) (- 1.0 a))
     (vec3/scalar-mul (vec3/create 0.5 0.7 1.0) a))))

(defn ray-color [ray world]
  (if-let [rec (h/hit-world world ray 0.001 Double/POSITIVE_INFINITY)]
    (let [n (:normal rec)]
      (vec3/scalar-mul (vec3/add n [1 1 1]) 0.5))
    (paint-sky ray)))

(defn write-color [color]
  (map (fn [c]
         (int (* 256 (utils/clamp c 0.0 0.999))))
       color))

(defn get-ray [i j]
  (let [u (+ i (rand))
        v (+ j (rand))
        pixel-center
        (vec3/add (vec3/scalar-mul pixel-delta-v v)
                  (vec3/add pixel00-loc
                            (vec3/scalar-mul pixel-delta-u u)))
        direction (vec3/sub pixel-center camera-center)]
    (ray/create camera-center direction)))

(defn pixel-color [i j]
  (let [accumulated
        (reduce
         (fn [color _]
           (let [r (get-ray i j)]
             (vec3/add color (ray-color r world))))
         (vec3/create 0 0 0)
         (range samples-per-pixel))]
    (vec3/scalar-mul accumulated pixel-samples-scale)))

(defn compute-pixel-color [x y]
  (write-color (pixel-color x y)))

