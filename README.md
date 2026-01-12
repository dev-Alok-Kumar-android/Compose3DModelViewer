# Compose3DModelViewer

A simple Android demo project that shows how to render and interact with a **3D GLB model** using **Jetpack Compose** and **SceneView**.

This project focuses on:

* Displaying a 3D model
* Basic camera zoom control
* Touch interactions
* Optional sound effects
* An animated Compose background layer

It is intended as a **learning / reference project**, not a full production app.

---

## ✨ Features

* Jetpack Compose UI
* GLB model rendering using SceneView
* Pinch zoom with configurable limits
* Single tap & double tap sound effects
* Optional animated background (Compose Canvas)
* Clean, minimal project structure

---

## 🧩 Tech Stack

* **Kotlin**
* **Jetpack Compose**
* **SceneView** (`2.2.1`)
* **Material 3**

---

## 📦 3D Model

* Format: `.glb`
* Default model: `robot.glb`
* Location: `assets/models/`

You can replace it with any GLB model.

---

## 🎮 Controls

* **Pinch gesture** → Zoom camera
* **Single tap on model** → Sound effect
* **Double tap on model** → Sound effect

Zoom behavior can be customized via parameters:

```kotlin
minZoom
maxZoom
zoomSpeed
```

---

## 🛠️ How to Run

1. Clone the repository:

   ```bash
   git clone https://github.com/dev-Alok-Kumar-android/Compose3DModelViewer.git
   ```

2. Open the project in **Android Studio**

3. Sync Gradle

4. Run on a **physical device** (recommended for 3D rendering)

---

## ⚠️ Notes

* This project uses **SceneView 2.2.1**
* Newer versions may cause gesture or camera issues
* Best tested on a real device rather than emulator

### Model Source

The default robot model used in this project was downloaded from Sketchfab:

* **Cute Robot Companion (GLB)**
* Source: https://sketchfab.com/3d-models/cute-robot-companion-glb-0f64197efce74fba8145b941efea323a

> A different GLB model can be used as long as it is compatible with SceneView.

All rights belong to the original creator.
This model is used here for demonstration and learning purposes only.

---

## 📄 License

This project is open for learning and experimentation.
You may modify or reuse the code for personal projects.
