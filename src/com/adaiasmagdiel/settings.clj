(ns com.adaiasmagdiel.settings
  (:require
    [clojure.math :as math]
    [com.adaiasmagdiel.raytracer.vec3 :as vec3]))

(def ^:const ASPECT_RATIO (double (/ 16 9)))

(def ^:const WIDTH 400)
(def ^:const HEIGHT (int (/ WIDTH ASPECT_RATIO)))

;; Camera

(def ^:const VFOV 90)
(def ^:const focal-length 1.0)
(def ^:const theta (math/to-radians VFOV))
(def ^:const h (math/tan (/ theta 2)))

(def ^:const VIEWPORT_HEIGHT (* 2 h focal-length))
(def ^:const VIEWPORT_WIDTH (* VIEWPORT_HEIGHT (/ (double WIDTH) HEIGHT)))

(def ^:const camera-center (vec3/create 0 0 0))
(def ^:const samples-per-pixel 10)
(def ^:const pixel-samples-scale (/ 1.0 samples-per-pixel))

(def ^:const viewport-u (vec3/create VIEWPORT_WIDTH 0 0))
(def ^:const viewport-v (vec3/create 0 (* -1 VIEWPORT_HEIGHT) 0))

(def ^:const pixel-delta-u (vec3/scalar-div viewport-u WIDTH))
(def ^:const pixel-delta-v (vec3/scalar-div viewport-v HEIGHT))

(def ^:const viewport-upper-left (vec3/sub
                                  camera-center
                                  (vec3/create 0 0 focal-length)
                                  (vec3/scalar-div viewport-u 2)
                                  (vec3/scalar-div viewport-v 2)))
(def ^:const pixel00-loc (vec3/add viewport-upper-left (vec3/scalar-mul (vec3/add pixel-delta-u pixel-delta-v) 0.5)))
