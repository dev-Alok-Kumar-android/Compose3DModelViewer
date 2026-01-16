package com.tuto.alokkumar.tuto3d.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tuto3d.R
import com.tuto.alokkumar.tuto3d.ui.components.AnimatedLinesBackground
import com.tuto.alokkumar.tuto3d.ui.components.SettingsSheet
import com.tuto.alokkumar.tuto3d.ui.theme.Tuto3dTheme
import io.github.sceneview.SceneView
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: AppViewModel) {
    val context = LocalContext.current
    var chainCount by rememberSaveable { mutableIntStateOf(50) }
    var showSettings by remember { mutableStateOf(false) }
    var soundEnabled by rememberSaveable { mutableStateOf(true) }
    var backgroundEnabled by rememberSaveable { mutableStateOf(true) }
    var quickShortcutsEnabled by rememberSaveable { mutableStateOf(false) }
    var isTigerElseRobot by rememberSaveable { mutableStateOf(false) }

    val demo: File = remember(isTigerElseRobot) {
        context.assetToCache(
            if (isTigerElseRobot) "models/tiger.glb" else "models/robot.glb"
        )
    }


    val customModelPath by viewModel.modelPath.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val modelTitle by viewModel.modelTitle.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(), onResult = { uri ->
            if (uri != null) {
                viewModel.loadModelFromUri(uri)
            }
        })


    val engine = remember { SceneView.createEngine(SceneView.createEglContext()) }
    val modelLoader = rememberModelLoader(engine)

    Tuto3dTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(), topBar = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.statusBars.asPaddingValues()),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (quickShortcutsEnabled) {
                        Column {
                            var modelInfoShowing by remember { mutableStateOf(false) }
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .clickable {
                                        modelInfoShowing = !modelInfoShowing
                                    }, horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (!modelInfoShowing) {
                                        "Help / Info ↓"
                                    } else {
                                        "Help / Info ↑ \n" + stringResource(R.string.model_info)
                                    }, textAlign = TextAlign.Center
                                )

                                AnimatedVisibility(modelInfoShowing) {
                                    Column(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(26.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Slider(
                                            value = chainCount.toFloat(),
                                            onValueChange = { newValue ->
                                                chainCount = newValue.toInt()
                                            },
                                            valueRange = 1f..300f,
                                            steps = 299
                                        )

                                        Text(
                                            text = "Lines: $chainCount",
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            viewModel.setModelTitle(modelTitle?:demo.name)
                        }

                        if (isLoading) {
                            val dots = rememberInfiniteTransition().animateFloat(
                                initialValue = 0f,
                                targetValue = 3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(900), repeatMode = RepeatMode.Restart
                                )
                            ).value

                            Text(
                                text = "Loading " + ".".repeat(dots.roundToInt().coerceIn(0, 3)).padEnd(3, ' '),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        } else Text(
                            text = modelTitle ?: demo.name,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row {
                        IconButton(onClick = {
                            launcher.launch(
                                arrayOf(
                                    "application/octet-stream",
                                    "model/gltf-binary",
                                    "model/gltf+json"
                                )
                            )
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Model")
                        }

                        IconButton(onClick = { showSettings = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            }) { innerPadding ->
            var globalTouchPos by remember { mutableStateOf(Offset.Unspecified) }
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            while (true) {
                                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                                val change = event.changes.firstOrNull()

                                if (change != null) {
                                    globalTouchPos =
                                        if (change.pressed) change.position else Offset.Unspecified
                                }
                            }
                        }
                    }) {

                if (backgroundEnabled) {
                    key(chainCount) {
                        AnimatedLinesBackground(
                            modifier = Modifier.fillMaxSize(),
                            touchPosition = globalTouchPos,
                            chainCount = chainCount
                        )
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                if (isSuccess == false) {
                    Text(
                        text = "Failed to load model",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }


                val modelInstance = remember(customModelPath) {
                    customModelPath.let { path ->
                        modelLoader.createModelInstance(File(path ?: demo.path))
                    }
                }

                My3DModelViewer(
                    modifier = Modifier.fillMaxSize(),
                    engine = engine,
                    modelLoader = modelLoader,
                    modelInstance = modelInstance,
                    soundEnabled = soundEnabled,
                    minZoom = if (isTigerElseRobot) -20f else -20f,
                    maxZoom = if (isTigerElseRobot) 30f else 0.7f,
                    initialModelScale = if (isTigerElseRobot) 10f else 0.5f,
                    canRotateX = !isTigerElseRobot
                )

                LaunchedEffect(isSuccess) {
                    if (isSuccess == false) {
                        delay(1500)
                        viewModel.clearCache()
                    }
                }

            }
        }

        if (showSettings) {
            SettingsSheet(
                soundEnabled = soundEnabled,
                backgroundEnabled = backgroundEnabled,
                quickShortcuts = quickShortcutsEnabled,
                onSoundChange = { soundEnabled = it },
                onBackgroundChange = { backgroundEnabled = it },
                chainCount = chainCount,
                onChainCountUpdate = { chainCount = it },
                onQuickShortcutChanged = { quickShortcutsEnabled = it },
                onDismiss = { showSettings = false })
        }
    }
}

private fun Context.assetToCache(assetPath: String): File {
    val outFile = File(cacheDir, assetPath.substringAfterLast("/"))
    assets.open(assetPath).use { input ->
        outFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return outFile
}
