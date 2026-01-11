package com.tuto.alokkumar.tuto3d.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tuto3d.R
import com.tuto.alokkumar.tuto3d.ui.components.AnimatedLinesBackground
import com.tuto.alokkumar.tuto3d.ui.components.SettingsSheet
import com.tuto.alokkumar.tuto3d.ui.theme.Tuto3dTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var chainCount by rememberSaveable { mutableIntStateOf(50) }
    var showSettings by remember { mutableStateOf(false) }
    var soundEnabled by rememberSaveable { mutableStateOf(true) }
    var backgroundEnabled by rememberSaveable { mutableStateOf(true) }
    var quickShortcutsEnabled by rememberSaveable { mutableStateOf(false) }


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
                            var robotInfoShowing by remember { mutableStateOf(false) }
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .clickable {
                                        robotInfoShowing = !robotInfoShowing
                                    }, horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (!robotInfoShowing) {
                                        stringResource(R.string.robot) + " ↓"
                                    } else {
                                        stringResource(R.string.robot) + " ↑ \n" + stringResource(R.string.robot_info)
                                    }, textAlign = TextAlign.Center
                                )

                                AnimatedVisibility(robotInfoShowing) {
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
                                            steps = 299 // optional but nice
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
                        Text(
                            stringResource(R.string.robot),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    IconButton(
                        onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings, contentDescription = "Settings"
                        )
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
                    }
            ) {

                if (backgroundEnabled) {
                    key(chainCount) {
                        AnimatedLinesBackground(
                            modifier = Modifier.fillMaxSize(),
                            touchPosition = globalTouchPos,
                            chainCount = chainCount
                        )
                    }
                }
                key(soundEnabled) {
                    My3DModelViewer(
                        modifier = Modifier.fillMaxSize(), soundEnabled = soundEnabled
                    )
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