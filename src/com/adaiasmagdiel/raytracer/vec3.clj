(ns com.adaiasmagdiel.raytracer.vec3
	(:require [clojure.math :as math]))

(defn create
	([] (create 0 0 0))
	([x y z] (vector x y z)))

(defn x [v] (nth v 0))
(defn y [v] (nth v 1))
(defn z [v] (nth v 2))

(defn add [& vecs]
  (apply mapv + vecs))
(defn sub [& vecs]
  (apply mapv - vecs))
(defn mul [& vecs]
  (apply mapv * vecs))
(defn div [& vecs]
  (apply mapv / vecs))

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
