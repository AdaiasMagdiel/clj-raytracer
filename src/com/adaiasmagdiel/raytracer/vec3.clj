(ns com.adaiasmagdiel.raytracer.vec3
  (:require [clojure.math :as math]
            [com.adaiasmagdiel.utils :as utils]))

(defn create
  ([] (create 0 0 0))
  ([x y z] (vector x y z)))

(defn x [v] (nth v 0))
(defn y [v] (nth v 1))
(defn z [v] (nth v 2))

(defn add
  ([v1 v2]
   (let [[x1 y1 z1] v1
         [x2 y2 z2] v2]
     [(+ x1 x2) (+ y1 y2) (+ z1 z2)]))
  ([v1 v2 & more]
   (apply mapv + v1 v2 more)))

(defn sub
  ([v1 v2]
   (let [[x1 y1 z1] v1
         [x2 y2 z2] v2]
     [(- x1 x2) (- y1 y2) (- z1 z2)]))
  ([v1 v2 & more]
   (apply mapv - v1 v2 more)))

(defn mul
  ([v1 v2]
   (let [[x1 y1 z1] v1
         [x2 y2 z2] v2]
     [(* x1 x2) (* y1 y2) (* z1 z2)]))
  ([v1 v2 & more]
   (apply mapv * v1 v2 more)))

(defn div
  ([v1 v2]
   (let [[x1 y1 z1] v1
         [x2 y2 z2] v2]
     [(/ x1 x2) (/ y1 y2) (/ z1 z2)]))
  ([v1 v2 & more]
   (apply mapv / v1 v2 more)))

(defn scalar-mul [[x y z] t]
  (vector (* t x) (* t y) (* t z)))
(defn scalar-div [v t]
  (scalar-mul v (/ 1.0 t)))

(defn length-sq [[x y z]]
  (+ (* x x) (* y y) (* z z)))
(defn length [v]
  (math/sqrt (length-sq v)))

(defn dot [[ux uy uz] [vx vy vz]]
  (+ (* ux vx) (* uy vy) (* uz vz)))
(defn cross [[ux uy uz] [vx vy vz]]
  (vector
    (- (* uy vz) (* uz vy))
    (- (* uz vx) (* ux vz))
    (- (* ux vy) (* uy vx))))

(defn unit [[x y z :as v]]
  (let [l (double (length v))]
    (vector (/ x l) (/ y l) (/ z l))))

(defn random [min max]
  [(utils/random-double min max)
   (utils/random-double min max)
   (utils/random-double min max)])

(defn random-unit-vector []
  (loop []
    (let [p (random -1.0 1.0)
          lensq (length-sq p)]
      (if (and (<= lensq 1.0) (> lensq 1e-160))
        (scalar-div p (Math/sqrt lensq))
        (recur)))))

(defn random-on-hemisphere [normal]
  (let [on-unit-sphere (random-unit-vector)]
    (if (> (dot on-unit-sphere normal) 0.0)
      on-unit-sphere
      (scalar-mul on-unit-sphere -1))))

(defn near-zero? [[x y z]]
  (let [s 1e-8]
    (and (< (abs x) s) 
         (< (abs y) s) 
         (< (abs z) s))))

(defn reflect [v n]
  (sub v (scalar-mul n (* 2 (dot v n)))))

(defn refract [uv n etai-over-etat]
  (let [cos-theta (min (dot (scalar-mul uv -1) n) 1.0)
        r-out-perp (scalar-mul (add uv (scalar-mul n cos-theta)) etai-over-etat)
        r-out-parallel (scalar-mul n (* -1 (math/sqrt (abs (- 1.0 (length-sq r-out-perp))))))]
    
    (add r-out-perp r-out-parallel)))

(defn random-in-unit-disk []
  (loop []
    (let [p (create (utils/random-double -1 1) (utils/random-double -1 1) 0)]
      (if (< (length-sq p) 1)
        p
        (recur)))))
