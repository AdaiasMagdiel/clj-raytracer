(ns com.adaiasmagdiel.settings
  (:require
    [clojure.math :as math]
    [com.adaiasmagdiel.raytracer.vec3 :as vec3]))

(def ^:const ASPECT_RATIO (double (/ 16 9)))

(def ^:const WIDTH 400)
(def ^:const HEIGHT (int (/ WIDTH ASPECT_RATIO)))

;; Camera

(def ^:const vfov 20)

(def ^:const lookfrom (vec3/create -2 2 1))
(def ^:const lookat   (vec3/create 0 0 -1))
(def ^:const vup      (vec3/create 0 1 0))

(def ^:const focal-length (vec3/length (vec3/sub lookfrom lookat)))
(def ^:const theta (math/to-radians vfov))
(def ^:const h (math/tan (/ theta 2)))

(def ^:const w (vec3/unit (vec3/sub lookfrom lookat)))
(def ^:const u (vec3/unit (vec3/cross vup w)))
(def ^:const v (vec3/cross w u))

(def ^:const VIEWPORT_HEIGHT (* 2 h focal-length))
(def ^:const VIEWPORT_WIDTH (* VIEWPORT_HEIGHT (/ (double WIDTH) HEIGHT)))

(def ^:const camera-center lookfrom)
(def ^:const samples-per-pixel 100)
(def ^:const pixel-samples-scale (/ 1.0 samples-per-pixel))
(def ^:const max-depth 50)

(def ^:const viewport-u (vec3/scalar-mul u VIEWPORT_WIDTH))
(def ^:const viewport-v (vec3/scalar-mul (vec3/scalar-mul v -1) VIEWPORT_HEIGHT))

(def ^:const pixel-delta-u (vec3/scalar-div viewport-u WIDTH))
(def ^:const pixel-delta-v (vec3/scalar-div viewport-v HEIGHT))

(def ^:const viewport-upper-left (vec3/sub
                                  camera-center
                                  (vec3/scalar-mul w focal-length)
                                  (vec3/scalar-div viewport-u 2)
                                  (vec3/scalar-div viewport-v 2)))
(def ^:const pixel00-loc (vec3/add viewport-upper-left (vec3/scalar-mul (vec3/add pixel-delta-u pixel-delta-v) 0.5)))
