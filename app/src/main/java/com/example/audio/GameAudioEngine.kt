package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object GameAudioEngine {
    private const val SAMPLE_RATE = 44100
    private var audioTrack: AudioTrack? = null
    private val isRunning = AtomicBoolean(false)
    private var audioThread: Thread? = null

    // Volume controls
    var musicVolume: Float = 0.7f
    var sfxVolume: Float = 0.85f
    var isMuted: Boolean = false

    // Active music track (0 to 3)
    @Volatile
    var currentTrackId: Int = 0

    // Callback for rhythmic visual pulsing (fraction 0f to 1f)
    var onBeatPulse: ((Float) -> Unit)? = null

    // Real-time SFX queue
    private val sfxTriggers = mutableListOf<SFX>()
    private val sfxLock = Any()

    enum class SFXType {
        JUMP, PAD, ORB, GRAVITY, COIN, DEATH, CHECKPOINT, WIN
    }

    private class SFX(val type: SFXType, var sampleIndex: Int = 0)

    fun start() {
        if (isRunning.get()) return
        isRunning.set(true)

        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            audioThread = Thread({ audioLoop() }, "GeoDashAudioThread").apply {
                priority = Thread.MAX_PRIORITY
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        isRunning.set(false)
        try {
            audioThread?.join(500)
        } catch (ignored: Exception) {}
        audioThread = null

        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (ignored: Exception) {}
        audioTrack = null
    }

    fun playJump() = triggerSFX(SFXType.JUMP)
    fun playPad() = triggerSFX(SFXType.PAD)
    fun playOrb() = triggerSFX(SFXType.ORB)
    fun playGravity() = triggerSFX(SFXType.GRAVITY)
    fun playCoin() = triggerSFX(SFXType.COIN)
    fun playDeath() = triggerSFX(SFXType.DEATH)
    fun playCheckpoint() = triggerSFX(SFXType.CHECKPOINT)
    fun playWin() = triggerSFX(SFXType.WIN)

    private fun triggerSFX(type: SFXType) {
        if (isMuted || sfxVolume <= 0f) return
        synchronized(sfxLock) {
            // Cap simultaneous SFX to avoid saturation
            if (sfxTriggers.size < 8) {
                sfxTriggers.add(SFX(type))
            }
        }
    }

    private fun audioLoop() {
        val chunkSize = 1024
        val buffer = ShortArray(chunkSize)
        var totalSamples: Long = 0

        // BPM: 130 BPM -> 2.166 beats/sec -> ~20353 samples per beat
        val samplesPerBeat = (SAMPLE_RATE * 60f / 130f).toInt()
        val samplesPer16th = samplesPerBeat / 4

        while (isRunning.get()) {
            if (isMuted) {
                buffer.fill(0)
                audioTrack?.write(buffer, 0, chunkSize)
                continue
            }

            for (i in 0 until chunkSize) {
                val sampleIdx = totalSamples + i
                val beatStep = (sampleIdx / samplesPer16th) % 64
                val beatProgress = (sampleIdx % samplesPerBeat).toFloat() / samplesPerBeat

                // Rhythmic beat pulse callback for UI animations (every quarter beat)
                if (i == 0 && (sampleIdx % samplesPerBeat) < chunkSize) {
                    val pulse = (1f - beatProgress).coerceIn(0f, 1f)
                    onBeatPulse?.invoke(pulse)
                }

                var musicSample = 0f

                if (musicVolume > 0f) {
                    // Drum Channel (Kick on 0, 4, 8, 12; Snare on 4, 12; Hi-hat on 2, 6, 10, 14)
                    val beat16 = (beatStep % 16).toInt()
                    val posIn16th = (sampleIdx % samplesPer16th).toInt()

                    // Kick (Four on the floor: 0, 4, 8, 12)
                    if (beat16 % 4 == 0) {
                        val kickPos = posIn16th.toFloat() / (samplesPer16th * 2)
                        if (kickPos < 1f) {
                            val kickFreq = 140f * (1f - kickPos * 0.75f) + 40f
                            val kickEnv = exp(-kickPos * 6.0).toFloat()
                            musicSample += sin(2.0 * PI * kickFreq * posIn16th / SAMPLE_RATE).toFloat() * kickEnv * 0.6f
                        }
                    }

                    // Snare / Clap on 4 and 12
                    if (beat16 == 4 || beat16 == 12) {
                        val snarePos = posIn16th.toFloat() / (samplesPer16th * 2)
                        if (snarePos < 1f) {
                            val noise = (Random.nextFloat() * 2f - 1f)
                            val snareTone = sin(2.0 * PI * 220.0 * posIn16th / SAMPLE_RATE).toFloat()
                            val snareEnv = exp(-snarePos * 8.0).toFloat()
                            musicSample += (noise * 0.7f + snareTone * 0.3f) * snareEnv * 0.45f
                        }
                    }

                    // Hi-hat on every off-beat 16th
                    if (beat16 % 2 == 1) {
                        val hatPos = posIn16th.toFloat() / samplesPer16th
                        if (hatPos < 0.5f) {
                            val noise = (Random.nextFloat() * 2f - 1f)
                            val hatEnv = exp(-hatPos * 14.0).toFloat()
                            musicSample += noise * hatEnv * 0.18f
                        }
                    }

                    // Bass & Lead melodies based on current track
                    val noteFreq = getMelodyFreq(currentTrackId, beatStep.toInt())
                    if (noteFreq > 0f) {
                        val t = posIn16th.toDouble() / SAMPLE_RATE
                        // Sawtooth / Square hybrid synth tone
                        val wave = (2.0 * ((noteFreq * t) % 1.0) - 1.0).toFloat()
                        val sub = sin(2.0 * PI * (noteFreq * 0.5) * t).toFloat()
                        val leadEnv = exp(-(posIn16th.toFloat() / samplesPer16th) * 2.5).toFloat()
                        musicSample += (wave * 0.25f + sub * 0.35f) * leadEnv * 0.5f
                    }
                }

                // Process SFX triggers
                var sfxSample = 0f
                synchronized(sfxLock) {
                    val it = sfxTriggers.iterator()
                    while (it.hasNext()) {
                        val sfx = it.next()
                        val sfxVal = renderSFX(sfx)
                        sfxSample += sfxVal
                        sfx.sampleIndex++
                        if (isSFXFinished(sfx)) {
                            it.remove()
                        }
                    }
                }

                val mixed = (musicSample * musicVolume + sfxSample * sfxVolume).coerceIn(-1f, 1f)
                buffer[i] = (mixed * 32767f).toInt().toShort()
            }

            totalSamples += chunkSize
            audioTrack?.write(buffer, 0, chunkSize)
        }
    }

    private fun getMelodyFreq(trackId: Int, step: Int): Float {
        // Melodies tuned for D-minor / A-minor energetic dance rhythms
        val scale = when (trackId) {
            1 -> floatArrayOf(220f, 261.6f, 293.7f, 329.6f, 392f, 440f, 523.3f) // Cyber
            2 -> floatArrayOf(146.8f, 174.6f, 196f, 220f, 233.1f, 293.7f, 349.2f) // Dark Demon
            3 -> floatArrayOf(164.8f, 196f, 220f, 246.9f, 293.7f, 329.6f, 392f) // Neon Pulse
            else -> floatArrayOf(196f, 220f, 261.6f, 293.7f, 329.6f, 392f, 440f) // Stereo Madness
        }

        val pattern = intArrayOf(
            0, 2, 4, 2, 0, 4, 3, 1,
            0, 2, 4, 5, 4, 2, 1, 0,
            2, 4, 6, 4, 2, 5, 4, 2,
            0, 3, 5, 4, 2, 1, 0, 0
        )
        val noteIdx = pattern[step % pattern.size]
        return scale[noteIdx % scale.size]
    }

    private fun renderSFX(sfx: SFX): Float {
        val s = sfx.sampleIndex
        return when (sfx.type) {
            SFXType.JUMP -> {
                // Pitch slide up 180Hz to 600Hz over 0.12s
                val duration = (SAMPLE_RATE * 0.12f).toInt()
                val progress = s.toFloat() / duration
                val freq = 180f + (progress * 420f)
                val env = (1f - progress).coerceAtLeast(0f)
                val square = if (sin(2.0 * PI * freq * s / SAMPLE_RATE) > 0) 1f else -1f
                square * env * 0.4f
            }
            SFXType.PAD -> {
                // Bright resonant jump 220Hz to 900Hz over 0.18s
                val duration = (SAMPLE_RATE * 0.18f).toInt()
                val progress = s.toFloat() / duration
                val freq = 220f + (progress * 680f)
                val env = (1f - progress).coerceAtLeast(0f)
                val sine = sin(2.0 * PI * freq * s / SAMPLE_RATE).toFloat()
                sine * env * 0.6f
            }
            SFXType.ORB -> {
                // High bell chime 880Hz + 1760Hz
                val duration = (SAMPLE_RATE * 0.2f).toInt()
                val progress = s.toFloat() / duration
                val env = exp(-progress * 5.0).toFloat()
                val tone = sin(2.0 * PI * 880.0 * s / SAMPLE_RATE).toFloat() * 0.6f +
                        sin(2.0 * PI * 1760.0 * s / SAMPLE_RATE).toFloat() * 0.4f
                tone * env * 0.5f
            }
            SFXType.GRAVITY -> {
                // Pitch shift sweep
                val duration = (SAMPLE_RATE * 0.15f).toInt()
                val progress = s.toFloat() / duration
                val freq = 500f - (progress * 300f)
                val env = (1f - progress).coerceAtLeast(0f)
                sin(2.0 * PI * freq * s / SAMPLE_RATE).toFloat() * env * 0.5f
            }
            SFXType.COIN -> {
                // Dual chime sparkle (B6 & E7)
                val duration = (SAMPLE_RATE * 0.35f).toInt()
                val progress = s.toFloat() / duration
                val env = exp(-progress * 4.0).toFloat()
                val f1 = 1975.5f
                val f2 = 2637.0f
                val sparkle = sin(2.0 * PI * f1 * s / SAMPLE_RATE).toFloat() * 0.5f +
                        sin(2.0 * PI * f2 * s / SAMPLE_RATE).toFloat() * 0.5f
                sparkle * env * 0.6f
            }
            SFXType.DEATH -> {
                // Heavy crunchy explosion + sub drop
                val duration = (SAMPLE_RATE * 0.45f).toInt()
                val progress = s.toFloat() / duration
                val noise = (Random.nextFloat() * 2f - 1f)
                val subFreq = 120f * (1f - progress * 0.7f) + 30f
                val sub = sin(2.0 * PI * subFreq * s / SAMPLE_RATE).toFloat()
                val env = exp(-progress * 5.0).toFloat()
                (noise * 0.65f + sub * 0.35f) * env * 0.8f
            }
            SFXType.CHECKPOINT -> {
                // Pleasant ding
                val duration = (SAMPLE_RATE * 0.15f).toInt()
                val progress = s.toFloat() / duration
                val env = exp(-progress * 8.0).toFloat()
                sin(2.0 * PI * 1046.5 * s / SAMPLE_RATE).toFloat() * env * 0.4f
            }
            SFXType.WIN -> {
                // Celebratory fanfare chime
                val duration = (SAMPLE_RATE * 0.8f).toInt()
                val progress = s.toFloat() / duration
                val step = (progress * 4).toInt()
                val freqs = floatArrayOf(523.3f, 659.3f, 783.9f, 1046.5f)
                val f = freqs[step.coerceIn(0, 3)]
                val env = (1f - progress * 0.8f).coerceAtLeast(0f)
                sin(2.0 * PI * f * s / SAMPLE_RATE).toFloat() * env * 0.6f
            }
        }
    }

    private fun isSFXFinished(sfx: SFX): Boolean {
        val s = sfx.sampleIndex
        val maxSamples = when (sfx.type) {
            SFXType.JUMP -> (SAMPLE_RATE * 0.12f).toInt()
            SFXType.PAD -> (SAMPLE_RATE * 0.18f).toInt()
            SFXType.ORB -> (SAMPLE_RATE * 0.2f).toInt()
            SFXType.GRAVITY -> (SAMPLE_RATE * 0.15f).toInt()
            SFXType.COIN -> (SAMPLE_RATE * 0.35f).toInt()
            SFXType.DEATH -> (SAMPLE_RATE * 0.45f).toInt()
            SFXType.CHECKPOINT -> (SAMPLE_RATE * 0.15f).toInt()
            SFXType.WIN -> (SAMPLE_RATE * 0.8f).toInt()
        }
        return s >= maxSamples
    }
}
