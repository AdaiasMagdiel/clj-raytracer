(ns com.adaiasmagdiel.engine
  (:require [com.adaiasmagdiel.settings :as s]
            [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]
            [com.adaiasmagdiel.raytracer.sphere :as sphere]
            [com.adaiasmagdiel.raytracer.hittable :as h]
            [com.adaiasmagdiel.utils :as utils]
            [com.adaiasmagdiel.raytracer.material :as mat]
            [clojure.math :as math]))

(def ^:const world [(sphere/create (vec3/create  0.0 -100.5 -1.0) 100 (mat/->Lambertian [0.8 0.8 0]))
                    (sphere/create (vec3/create  0.0 0.0 -1.2)    0.5 (mat/->Lambertian [0.1 0.2 0.5]))
                    (sphere/create (vec3/create -1.0 0.0 -1.0)    0.5 (mat/->Dielectric 1.50))
                    (sphere/create (vec3/create -1.0 0.0 -1.0)    0.4 (mat/->Dielectric (/ 1.0 1.50)))
                    (sphere/create (vec3/create  1.0 0.0 -1.0)    0.5 (mat/->Metal [0.8 0.6 0.2] 1.0))])

(defn paint-sky [r]
  (let [unit-direction (vec3/unit (:direction r))
        a (* 0.5 (+ 1.0 (vec3/y unit-direction)))]
    (vec3/add
     (vec3/scalar-mul (vec3/create 1.0 1.0 1.0) (- 1.0 a))
     (vec3/scalar-mul (vec3/create 0.5 0.7 1.0) a))))

(defn ray-color
  ([ray world] (ray-color ray world s/max-depth))
  ([ray world depth]
   (if (<= depth 0)
     [0 0 0]

     (if-let [rec (h/hit-world world ray 0.001 Double/POSITIVE_INFINITY)]
       (let [material (:material rec)]
         (if-let [scatter-result (mat/scatter material ray rec)]
           
           (let [{:keys [scattered attenuation]} scatter-result]
             (vec3/mul attenuation (ray-color scattered world (dec depth))))
           
           [0 0 0]))

       (paint-sky ray)))))

(defn linear-to-gama [linear-component]
  (if (> linear-component 0)
    (math/sqrt linear-component)
    0))

(defn write-color [color]
  (->> color
       (map linear-to-gama)
       (map #(utils/clamp % 0.0 0.999))
       (map #(* 256 %))
       (map int)))

(defn defocus-disk-sample []
  (let [p (vec3/random-in-unit-disk)]
    (vec3/add
     s/camera-center (vec3/scalar-mul s/defocus-disk-u (vec3/x p)) (vec3/scalar-mul s/defocus-disk-v (vec3/y p)))))

(defn get-ray [i j]
  (let [u (+ i (rand))
        v (+ j (rand))
        pixel-center
        (vec3/add (vec3/scalar-mul s/pixel-delta-v v)
                  (vec3/add s/pixel00-loc
                            (vec3/scalar-mul s/pixel-delta-u u)))
        origin (if (<= s/defocus-angle 0) s/camera-center (defocus-disk-sample)) 
        direction (vec3/sub pixel-center origin)]
    (ray/create origin direction)))

(defn pixel-color [i j]
  (let [accumulated
        (reduce
         (fn [color _]
           (let [r (get-ray i j)]
             (vec3/add color (ray-color r world))))
         (vec3/create 0 0 0)
         (range s/samples-per-pixel))]
    (vec3/scalar-mul accumulated s/pixel-samples-scale)))

(defn compute-pixel-color [x y]
  (write-color (pixel-color x y)))
