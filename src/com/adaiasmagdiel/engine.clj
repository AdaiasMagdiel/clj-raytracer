(ns com.adaiasmagdiel.engine
  (:require [com.adaiasmagdiel.settings :as s]
            [com.adaiasmagdiel.raytracer.vec3 :as vec3]
            [com.adaiasmagdiel.raytracer.ray :as ray]
            [com.adaiasmagdiel.raytracer.sphere :as sphere]
            [com.adaiasmagdiel.raytracer.hittable :as h]
            [com.adaiasmagdiel.utils :as utils]
            [com.adaiasmagdiel.raytracer.material :as mat]
            [clojure.math :as math])
  (:import [com.adaiasmagdiel.raytracer.vec3 Vec3]
           [com.adaiasmagdiel.raytracer.ray Ray]
           [com.adaiasmagdiel.raytracer.hittable HitRecord IHittable]
           [java.util.concurrent ThreadLocalRandom]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

;; Cache de cores constantes para evitar chamadas de função
(def ^:const COLOR-BLACK (vec3/create 0 0 0))
(def ^:const COLOR-WHITE (vec3/create 1 1 1))
(def ^:const COLOR-SKY   (vec3/create 0.5 0.7 1.0))

(def world
  (into-array IHittable
              [(sphere/create (vec3/create  0.0 -100.5 -1.0) 100 (mat/->Lambertian (vec3/create 0.8 0.8 0)))
               (sphere/create (vec3/create  0.0 0.0 -1.2)    0.5 (mat/->Lambertian (vec3/create 0.1 0.2 0.5)))
               (sphere/create (vec3/create -1.0 0.0 -1.0)    0.5 (mat/->Dielectric 1.50))
               (sphere/create (vec3/create -1.0 0.0 -1.0)    0.4 (mat/->Dielectric (/ 1.0 1.50)))
               (sphere/create (vec3/create  1.0 0.0 -1.0)    0.5 (mat/->Metal (vec3/create 0.8 0.6 0.2) 1.0))]))

(defn paint-sky [^Ray r]
  (let [^Vec3 dir (.direction r)
        unit-direction (vec3/unit dir)
        a (* 0.5 (+ 1.0 (vec3/y unit-direction)))]
    (vec3/add
     (vec3/scalar-mul COLOR-WHITE (- 1.0 a))
     (vec3/scalar-mul COLOR-SKY a))))

(defn ray-color
  ([^Ray ray world] (ray-color ray world s/max-depth))
  ([^Ray ray world ^long depth]
   (if (<= depth 0)
     (vec3/create 0 0 0)

     (if-let [rec (h/hit-world world ray 0.001 Double/POSITIVE_INFINITY)]
       (let [material (.material ^HitRecord rec)]
         (if-let [scatter-result (mat/scatter material ray rec)]
           
           (let [{:keys [scattered attenuation]} scatter-result]
             (vec3/mul attenuation (ray-color scattered world (unchecked-dec depth))))
           
           (vec3/create 0 0 0)))

       (paint-sky ray)))))

(defn linear-to-gama ^double [^double linear-component]
  (if (> linear-component 0.0)
    (math/sqrt linear-component)
    0.0))

(defn write-color [color]
  (let [^Vec3 c color
        cx (.x c)
        cy (.y c)
        cz (.z c)

        r (int (* 256.0 (utils/clamp-double (linear-to-gama cx) 0.0 0.999)))
        g (int (* 256.0 (utils/clamp-double (linear-to-gama cy) 0.0 0.999)))
        b (int (* 256.0 (utils/clamp-double (linear-to-gama cz) 0.0 0.999)))]
    [r g b]))

(defn defocus-disk-sample []
  (let [p (vec3/random-in-unit-disk)]
    (vec3/add
     s/camera-center (vec3/scalar-mul s/defocus-disk-u (vec3/x p)) (vec3/scalar-mul s/defocus-disk-v (vec3/y p)))))

(defn get-ray [^long i ^long j]
  (let [rng (ThreadLocalRandom/current)
        u (+ (double i) (.nextDouble rng))
        v (+ (double j) (.nextDouble rng))
        pixel-center
        (vec3/add (vec3/scalar-mul s/pixel-delta-v v)
                  (vec3/add s/pixel00-loc
                            (vec3/scalar-mul s/pixel-delta-u u)))
        origin (if (<= s/defocus-angle 0)
                 s/camera-center
                 (defocus-disk-sample))
        direction (vec3/sub pixel-center origin)]
    (ray/create origin direction)))


(defn pixel-color [^long i ^long j]
  (loop [n 0
         acc (vec3/create 0 0 0)]
    (if (< n s/samples-per-pixel)
      (let [r (get-ray i j)]
        (recur (unchecked-inc n)
               (vec3/add acc (ray-color r world))))
      (vec3/scalar-mul acc s/pixel-samples-scale))))

(defn compute-pixel-color [x y]
  (write-color (pixel-color x y)))
