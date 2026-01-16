package com.tuto.alokkumar.tuto3d.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tuto3d.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    soundEnabled: Boolean,
    backgroundEnabled: Boolean,
    quickShortcuts: Boolean,
    chainCount: Int,
    onChainCountUpdate: (Int) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onBackgroundChange: (Boolean) -> Unit,
    onQuickShortcutChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = "Settings", style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            SettingRow(
                title = "Sound effects", checked = soundEnabled, onCheckedChange = onSoundChange
            )

            SettingRow(
                title = "Animated background",
                checked = backgroundEnabled,
                onCheckedChange = onBackgroundChange
            )

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(backgroundEnabled) {
                Column {
                    SettingRow(
                        title = "Show Quick Shortcuts",
                        checked = quickShortcuts,
                        onCheckedChange = onQuickShortcutChanged
                    )

                    Spacer(Modifier.height(24.dp))
                    Column {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            var chainCountLocal by remember { mutableIntStateOf(chainCount) }
                            Slider(
                                value = chainCountLocal.toFloat(),
                                onValueChange = { newValue ->
                                    chainCountLocal = newValue.toInt()
                                },
                                valueRange = 1f..300f, steps = 299,
                            )
                            Row {
                                Text(
                                    text = "Lines: $chainCountLocal",
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.weight(1f))
                                Button(onClick = { onChainCountUpdate(chainCountLocal) }) {
                                    Text("Update")
                                }
                            }

                        }
                        var modelInfoShowing by remember { mutableStateOf(false) }
                        Text(
                            text = if (!modelInfoShowing) {
                                "Help / Info  ↓"
                            } else {
                                  "Help / Info ↑ \n" + stringResource(R.string.model_info)
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    modelInfoShowing = !modelInfoShowing
                                }, textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SettingRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        Switch(
            checked = checked, onCheckedChange = onCheckedChange
        )
    }
}
