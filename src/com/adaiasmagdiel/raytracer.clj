(ns com.adaiasmagdiel.raytracer
  (:gen-class)
  (:require [com.adaiasmagdiel.quill :as q]
            [com.adaiasmagdiel.image :as img]))

(defn -main
  "Main entry point for the raytracer application."
  [& args]
  (if (= (first args) "--image")
    (img/main)
    (q/main)))
