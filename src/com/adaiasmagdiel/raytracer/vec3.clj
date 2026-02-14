(ns com.adaiasmagdiel.raytracer.vec3
  (:require [clojure.math :as math]
            [com.adaiasmagdiel.utils :as utils])
  (:import [clojure.lang Indexed Seqable Counted]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

;; --- TIPO DE ALTA PERFORMANCE ---
;; Implementa interfaces para se comportar como vetor (compatibilidade)
(deftype Vec3 [^double x ^double y ^double z]
  Indexed
  (nth [_ i] (case i 0 x 1 y 2 z (throw (IndexOutOfBoundsException.))))
  (nth [_ i not-found] (case i 0 x 1 y 2 z not-found))

  Seqable
  (seq [_] (list x y z)) ;; Permite usar map, reduce e destructuring

  Counted
  (count [_] 3)

  Object
  (toString [_] (str "[" x " " y " " z "]"))
  (equals [this other]
    (and (instance? Vec3 other)
         (== x (.x ^Vec3 other))
         (== y (.y ^Vec3 other))
         (== z (.z ^Vec3 other))))
  (hashCode [_]
    (java.util.Objects/hash (into-array Object [x y z]))))

;; Construtor seguro que força cast para double
(defn create
  (^Vec3 [] (Vec3. 0.0 0.0 0.0))
  (^Vec3 [x y z] (Vec3. (double x) (double y) (double z))))

;; Acesso direto aos campos (muito rápido)
(defn x ^double [^Vec3 v] (.x v))
(defn y ^double [^Vec3 v] (.y v))
(defn z ^double [^Vec3 v] (.z v))

;; --- ARITMÉTICA (Mantendo todas as assinaturas) ---

(defn add
  (^Vec3 [^Vec3 v1 ^Vec3 v2]
   (Vec3. (+ (.x v1) (.x v2)) (+ (.y v1) (.y v2)) (+ (.z v1) (.z v2))))
  (^Vec3 [^Vec3 v1 ^Vec3 v2 & more]
   (reduce add (add v1 v2) more)))

(defn sub
  (^Vec3 [^Vec3 v1 ^Vec3 v2]
   (Vec3. (- (.x v1) (.x v2)) (- (.y v1) (.y v2)) (- (.z v1) (.z v2))))
  (^Vec3 [^Vec3 v1 ^Vec3 v2 & more]
   (reduce sub (sub v1 v2) more)))

(defn mul
  (^Vec3 [^Vec3 v1 ^Vec3 v2]
   (Vec3. (* (.x v1) (.x v2)) (* (.y v1) (.y v2)) (* (.z v1) (.z v2))))
  (^Vec3 [^Vec3 v1 ^Vec3 v2 & more]
   (reduce mul (mul v1 v2) more)))

(defn div
  (^Vec3 [^Vec3 v1 ^Vec3 v2]
   (Vec3. (/ (.x v1) (.x v2)) (/ (.y v1) (.y v2)) (/ (.z v1) (.z v2))))
  (^Vec3 [^Vec3 v1 ^Vec3 v2 & more]
   (reduce div (div v1 v2) more)))

(defn scalar-mul ^Vec3 [^Vec3 v t]
  (let [t (double t)] ;; Garante que inteiros não quebrem
    (Vec3. (* (.x v) t) (* (.y v) t) (* (.z v) t))))

(defn scalar-div ^Vec3 [^Vec3 v t]
  (scalar-mul v (/ 1.0 (double t))))

;; --- MATEMÁTICA VETORIAL ---

(defn length-sq ^double [^Vec3 v]
  (let [vx (.x v) vy (.y v) vz (.z v)]
    (+ (* vx vx) (* vy vy) (* vz vz))))

(defn length ^double [^Vec3 v]
  (math/sqrt (length-sq v)))

(defn dot ^double [^Vec3 v1 ^Vec3 v2]
  (+ (* (.x v1) (.x v2))
     (* (.y v1) (.y v2))
     (* (.z v1) (.z v2))))

(defn cross ^Vec3 [^Vec3 v1 ^Vec3 v2]
  (let [x1 (.x v1) y1 (.y v1) z1 (.z v1)
        x2 (.x v2) y2 (.y v2) z2 (.z v2)]
    (Vec3. (- (* y1 z2) (* z1 y2))
           (- (* z1 x2) (* x1 z2))
           (- (* x1 y2) (* y1 x2)))))

(defn unit ^Vec3 [^Vec3 v]
  (let [len (length v)]
    (if (zero? len)
      (create 0 0 0)
      (scalar-div v len))))

;; --- ALEATÓRIOS ---

(defn random ^Vec3 [min max]
  (Vec3. (utils/random-double min max)
         (utils/random-double min max)
         (utils/random-double min max)))

(defn random-unit-vector ^Vec3 []
  (loop []
    (let [p (random -1.0 1.0)
          lensq (length-sq p)]
      (if (and (<= lensq 1.0) (> lensq 1e-160))
        (scalar-div p (math/sqrt lensq))
        (recur)))))

(defn random-on-hemisphere ^Vec3 [normal]
  (let [on-unit-sphere (random-unit-vector)]
    (if (> (dot on-unit-sphere normal) 0.0)
      on-unit-sphere
      (scalar-mul on-unit-sphere -1.0))))

(defn random-in-unit-disk ^Vec3 []
  (loop []
    (let [p (create (utils/random-double -1 1) (utils/random-double -1 1) 0)]
      (if (< (length-sq p) 1)
        p
        (recur)))))

;; --- UTILITÁRIOS ---

(defn near-zero? [^Vec3 v]
  (let [s 1e-8]
    (and (< (abs (.x v)) s)
         (< (abs (.y v)) s)
         (< (abs (.z v)) s))))

(defn reflect ^Vec3 [^Vec3 v ^Vec3 n]
  (sub v (scalar-mul n (* 2.0 (dot v n)))))

(defn refract ^Vec3 [^Vec3 uv ^Vec3 n ^double etai-over-etat]
  (let [cos-theta (min (dot (scalar-mul uv -1.0) n) 1.0)
        r-out-perp (scalar-mul (add uv (scalar-mul n cos-theta)) etai-over-etat)
        r-out-parallel (scalar-mul n (* -1.0 (math/sqrt (abs (- 1.0 (length-sq r-out-perp))))))]
    (add r-out-perp r-out-parallel)))
