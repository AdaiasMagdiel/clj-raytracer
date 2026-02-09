(ns com.adaiasmagdiel.engine
  (:require [clojure.math :as math]
            [com.adaiasmagdiel.settings :as s]
            [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]))

(def focal-length 1.0)
(def camera-center (vec3/create 0 0 0))

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

(defn paint-sky [r]
  (let [unity-direction (vec3/unit (:direction r))
        a (* 0.5 (+ 1.0 (vec3/y unity-direction)))]
    (vec3/add
     (vec3/scalar-mul (vec3/create 1.0 1.0 1.0) (- 1.0 a))
     (vec3/scalar-mul (vec3/create 0.5 0.7 1.0) a))))

(defn hit-sphere [center radius r]
  (let [oc (vec3/sub center (:origin r))
        ray-direction (:direction r)
        a (vec3/dot ray-direction ray-direction)
        b (* -2.0 (vec3/dot ray-direction oc))
        c (- (vec3/dot oc oc) (* radius radius))
        discriminant (- (* b b) (* 4 a c))]
    (if (< discriminant 0)
      -1.0
      (/ (- (* -1 b) (math/sqrt discriminant)) (* 2.0 a)))))

(defn ray-color [r]
  (let [t (hit-sphere (vec3/create 0 0 -1) 0.5 r)]
    (if (> t 0.0)
      (let [N (vec3/unit (vec3/sub (ray/at r t) (vec3/create 0 0 -1)))]
        (vec3/scalar-mul [(+ 1 (vec3/x N)) (+ 1 (vec3/y N)) (+ 1 (vec3/z N))] 0.5))
      (paint-sky r))))

(defn compute-pixel-color [x y]
  (let [pixel-center
        (vec3/add (vec3/scalar-mul pixel-delta-v y)
                  (vec3/add pixel00-loc (vec3/scalar-mul pixel-delta-u x)))
        ray-direction (vec3/sub pixel-center camera-center)
        r (ray/create camera-center ray-direction)]

    (map #(int (* 255.999 %)) (ray-color r))))
