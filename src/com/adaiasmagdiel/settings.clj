(ns com.adaiasmagdiel.settings)

(def ASPECT_RATIO (double (/ 16 9)))

(def WIDTH 400)
(def HEIGHT (int (/ WIDTH ASPECT_RATIO)))

(def VIEWPORT_HEIGHT 2.0)
(def VIEWPORT_WIDTH (* VIEWPORT_HEIGHT (/ (double WIDTH) HEIGHT)))
