package com.example.ui

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.GameAudioEngine

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit
) {
    var musicVol by remember { mutableFloatStateOf(GameAudioEngine.musicVolume) }
    var sfxVol by remember { mutableFloatStateOf(GameAudioEngine.sfxVolume) }
    var isMuted by remember { mutableStateOf(GameAudioEngine.isMuted) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF7C4DFF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SETTINGS",
                    color = Color(0xFF7C4DFF),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mute toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = "Mute",
                        tint = if (isMuted) Color.Red else Color.Green
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isMuted) "Audio Muted" else "Audio Enabled",
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = !isMuted,
                        onCheckedChange = {
                            isMuted = !it
                            GameAudioEngine.isMuted = isMuted
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00E5FF),
                            checkedTrackColor = Color(0xFF005577)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Music volume
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music Volume",
                            tint = Color(0xFF00E5FF)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Music Volume: ${(musicVol * 100).toInt()}%",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                    }
                    Slider(
                        value = musicVol,
                        onValueChange = {
                            musicVol = it
                            GameAudioEngine.musicVolume = it
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF00E5FF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SFX volume
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "SFX Volume",
                            tint = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SFX Volume: ${(sfxVol * 100).toInt()}%",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                    }
                    Slider(
                        value = sfxVol,
                        onValueChange = {
                            sfxVol = it
                            GameAudioEngine.sfxVolume = it
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFD700),
                            activeTrackColor = Color(0xFFFFD700)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
