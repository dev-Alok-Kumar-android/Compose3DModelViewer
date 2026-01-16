package com.tuto.alokkumar.tuto3d.ui

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.google.android.filament.Engine
import com.google.android.filament.gltfio.FilamentInstance
import com.tuto.alokkumar.tuto3d.R
import io.github.sceneview.Scene
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes

@Composable
fun My3DModelViewer(
    modifier: Modifier = Modifier,
    engine: Engine,
    modelInstance: FilamentInstance,
    modelLoader: ModelLoader = rememberModelLoader(engine),
    tapSoundResId: Int = R.raw.pop,
    doubleTapSoundResId: Int = R.raw.tf_notification,
    minZoom: Float = -1f,
    maxZoom: Float = 1f,
    zoomSpeed: Float = 0.0015f,
    minPan: Float = -1f,
    maxPan: Float = 1f,
    basePanSpeed: Float = 0.0003f,
    soundEnabled: Boolean = true,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
    rotateEnabled: Boolean = true,
    lockRotateXon90: Boolean = false,
    canRotateX: Boolean = true,
    canRotateY: Boolean = true,
    initialModelScale: Float = 0.5f,
    backgroundColor: Color = Color.Transparent,
) {
    val context = LocalContext.current

    val nodes = rememberNodes()

    var modelRotationY by remember { mutableFloatStateOf(0f) }
    var modelRotationX by remember { mutableFloatStateOf(0f) }
    var modelOffset by remember { mutableStateOf(Position(0f, 0f, 0f)) }

    val popSound = remember(context) { MediaPlayer.create(context, tapSoundResId) }
    val tfSound = remember(context) { MediaPlayer.create(context, doubleTapSoundResId) }

    Box(
        modifier = modifier
            .then(Modifier.background(backgroundColor))
            .pointerInput(zoomEnabled, panEnabled, rotateEnabled) {
                var lastDistance = 0f
                fun wrap360(angle: Float): Float {
                    val a = angle % 360f
                    return if (a < 0f) a + 360f else a
                }
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.filter { it.pressed }

                        when (pressed.size) {
                            // ☝️ ONE FINGER → ROTATE
                            1 -> {
                                lastDistance = 0f
                                if (rotateEnabled) {
                                    val change = pressed[0]
                                    val dx = change.position.x - change.previousPosition.x
                                    val dy = change.position.y - change.previousPosition.y

                                    val newRotY =
                                        if (canRotateY) wrap360(modelRotationY + dx * 0.4f) else modelRotationY
                                    modelRotationY = newRotY

                                    val newRotX =
                                        if (canRotateX) wrap360(modelRotationX + dy * 0.4f) else modelRotationX
                                    modelRotationX = if (lockRotateXon90) {
                                        newRotX.coerceIn(-90f, 90f)
                                    } else {
                                        newRotX
                                    }

                                    change.consume()
                                }
                            }

                            // 🤏 TWO FINGERS → PAN + ZOOM
                            2 -> {
                                val p1 = pressed[0]
                                val p2 = pressed[1]

                                // ZOOM logic
                                if (zoomEnabled) {
                                    val currentDistance = (p1.position - p2.position).getDistance()
                                    if (lastDistance > 0f) {
                                        val rawZoom = currentDistance - lastDistance
                                        val newZ = (modelOffset.z + rawZoom * zoomSpeed).coerceIn(
                                            minZoom,
                                            maxZoom
                                        )
                                        modelOffset = modelOffset.copy(z = newZ)
                                    }
                                    lastDistance = currentDistance
                                }

                                // PAN logic
                                if (panEnabled) {
                                    val center = (p1.position + p2.position) / 2f
                                    val prevCenter =
                                        (p1.previousPosition + p2.previousPosition) / 2f
                                    val pan = center - prevCenter

                                    val zoomProgress =
                                        ((maxZoom - modelOffset.z) / (maxZoom - minZoom)).coerceIn(
                                            0f,
                                            1f
                                        )
                                    val panSpeed = basePanSpeed * (1f + zoomProgress * 3f)

                                    modelOffset = modelOffset.copy(
                                        x = (modelOffset.x + pan.x * panSpeed).coerceIn(
                                            minPan,
                                            maxPan
                                        ),
                                        y = (modelOffset.y - pan.y * panSpeed).coerceIn(
                                            minPan,
                                            maxPan
                                        )
                                    )
                                }

                                pressed.forEach { it.consume() }
                            }

                            else -> {
                                lastDistance = 0f
                                break
                            }
                        }
                    }
                }
            }
    ) {
        Scene(
            modifier = Modifier,
            engine = engine,
            modelLoader = modelLoader,
            childNodes = nodes,
            isOpaque = false,
            cameraManipulator = null,
            onGestureListener = null
        ) {
            nodes.filterIsInstance<ModelNode>().firstOrNull()?.apply {
                rotation = Rotation(modelRotationX, modelRotationY, 0f)
                position = modelOffset
            }
        }
    }

    LaunchedEffect(modelInstance, soundEnabled) {
        nodes.removeAll { it is ModelNode }
        nodes += ModelNode(
            modelInstance = modelInstance,
            scaleToUnits = initialModelScale
        ).apply {
            isEditable = false
            onSingleTapConfirmed = {
                if (soundEnabled) popSound.start()
                true
            }
            onDoubleTap = {
                if (soundEnabled) tfSound.start()
                true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            popSound.release()
            tfSound.release()
        }
    }
}