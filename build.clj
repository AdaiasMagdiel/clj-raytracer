(ns build
  (:require [clojure.tools.build.api :as b]))

;; Project metadata
(def lib 'meu-projeto/app)
(def version "0.1.0-SNAPSHOT")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(defn clean []
  (b/delete {:path "target"}))

(defn uber []
  (clean)
  (b/copy-dir {:src-dirs ["src"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :main 'com.adaiasmagdiel.raytracer}))
