(ns com.adaiasmagdiel.quill
  (:require [quil.core :as q]
            [com.adaiasmagdiel.settings :as s]
            [com.adaiasmagdiel.engine :as engine]
            [com.adaiasmagdiel.utils :as utils]))

(defn setup []
  (let [img (q/create-image s/WIDTH s/HEIGHT :rgb)]

    (dotimes [y s/HEIGHT]
      (utils/log-progress (- s/HEIGHT y 1) s/HEIGHT)

      (dotimes [x s/WIDTH]
        (let [[r g b] (engine/compute-pixel-color x y)]

          (q/set-pixel img x y (q/color r g b)))))

    (q/image img 0 0)))

(defn main []
  (q/defsketch raytracer
    :title "Raytracer"
    :settings #(q/smooth 2)
    :setup setup
    :size [s/WIDTH s/HEIGHT]))
