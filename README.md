# clj-raytracer

<p align="center">
  <img src="image.webp" width="1280" height="720">
</p>

A pure Clojure implementation of the famous ["Ray Tracing in One Weekend"](https://raytracing.github.io/books/RayTracingInOneWeekend.html) book. 

This project explores functional programming patterns applied to computer graphics, using [Quil](https://quil.info/) (a Clojure wrapper for Processing) for real-time rendering and visualization.

## Features

- **Real-time Preview:** Integrated with Quil to visualize the rendering process directly in a window.
- **Pure Clojure:** Built with the latest Clojure 1.12 features and `tools.build`.

## Prerequisites

You need the [Clojure CLI tool](https://clojure.org/guides/install_clojure) installed on your machine.

## How to Run

### Preview

To use Quil to render a live preview:

```bash
clj -M:run
```

### Image

To generate an image:

```bash
clj -M:run --image
```

This will generate a `.ppm` image in the project root directory.
You can convert it to another format using any image conversion tool, such as `ffmpeg`:

```bash
ffmpeg -i image.ppm image.webp
```

## Building

To package the project into a standalone executable JAR:

```bash
clj -T:build uber

```

The resulting file will be located at `target/raytracer-0.1.0-SNAPSHOT-standalone.jar`.

## Project Structure

* `src/com/adaiasmagdiel/raytracer/vec3.clj`: Core vector math library.
* `src/com/adaiasmagdiel/raytracer.clj`: Main entry point and Quil sketch configuration.
* `build.clj`: Build script for generating artifacts.

## License

Copyright © 2026 Adaías Magdiel

Distributed under the MIT License. See [LICENSE](LICENSE) file for details.
