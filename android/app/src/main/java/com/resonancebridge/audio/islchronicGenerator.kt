package com.resonancebridge.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import kotlin.math.PI
import kotlin.math.sin

/**
 * Advanced isochronic tone generator with:
 * - Precise 7.83 Hz Schumann resonance
 * - Harmonics support
 * - Real-time modulation
 * - Energy-efficient rendering
 */
class IsochronicGenerator(context: Context) {
    
    companion object {
        const val SCHUMANN_FUNDAMENTAL = 7.83f
        const val SCHUMANN_HARMONIC_1 = 14.3f
        const val SCHUMANN_HARMONIC_2 = 20.8f
        const val SAMPLE_RATE = 48000 // Professional audio quality
        private const val AMPLITUDE_SCALE = 0.25f // Safe volume level
    }
    
    // Configuration
    data class Config(
        val baseFrequency: Float = SCHUMANN_FUNDAMENTAL,
        val carrierFrequency: Float = 200f,
        val useHarmonics: Boolean = false,
        val harmonicMix: Float = 0.2f, // 0-1, harmonic intensity
        val volume: Float = 0.5f,
        val modulationDepth: Float = 0.8f // 0-1, how pronounced the pulse is
    )
    
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var currentConfig = Config()
    private var renderThread: Thread? = null
    
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    
    private val audioFormat = AudioFormat.Builder()
        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
        .setSampleRate(SAMPLE_RATE)
        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
        .build()
    
    fun start(config: Config = currentConfig) {
        if (isPlaying) return
        
        currentConfig = config
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2 // Double buffer for smoothness
        
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(audioAttributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferSize)
            .build()
        
        audioTrack?.volume = config.volume * AMPLITUDE_SCALE
        
        isPlaying = true
        renderThread = Thread(RenderRunnable())
        renderThread?.priority = Thread.MAX_PRIORITY
        renderThread?.start()
        
        audioTrack?.play()
    }
    
    fun updateConfig(config: Config) {
        currentConfig = config
        audioTrack?.volume = config.volume * AMPLITUDE_SCALE
    }
    
    fun stop() {
        isPlaying = false
        renderThread?.interrupt()
        renderThread = null
        
        audioTrack?.apply {
            stop()
            release()
        }
        audioTrack = null
    }
    
    fun isPlaying(): Boolean = isPlaying
    
    fun getCurrentFrequency(): Float = currentConfig.baseFrequency
    
    private inner class RenderRunnable : Runnable {
        override fun run() {
            val samplesPerCycle = (SAMPLE_RATE / currentConfig.baseFrequency).toInt()
            val buffer = ShortArray(samplesPerCycle)
            
            while (isPlaying && !Thread.currentThread().isInterrupted) {
                renderBuffer(buffer)
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
        
        private fun renderBuffer(buffer: ShortArray) {
            val samples = buffer.size
            val maxAmplitude = Short.MAX_VALUE * AMPLITUDE_SCALE
            
            for (i in 0 until samples) {
                val time = i.toFloat() / SAMPLE_RATE
                
                // 1. Calculate envelope (pulse at baseFrequency)
                val phase = (time * currentConfig.baseFrequency) % 1.0f
                val envelope = if (phase < currentConfig.modulationDepth) 1.0f else 0.0f
                
                // 2. Carrier wave (audible tone)
                val carrier = sin(2.0f * PI.toFloat() * currentConfig.carrierFrequency * time)
                
                // 3. Harmonics (optional)
                var harmonicComponent = 0f
                if (currentConfig.useHarmonics) {
                    harmonicComponent = 
                        sin(2.0f * PI.toFloat() * SCHUMANN_HARMONIC_1 * time) * 0.5f +
                        sin(2.0f * PI.toFloat() * SCHUMANN_HARMONIC_2 * time) * 0.3f
                }
                
                // 4. Mix everything
                val sampleValue = carrier * envelope + 
                                 harmonicComponent * currentConfig.harmonicMix
                
                buffer[i] = (sampleValue * maxAmplitude).toInt().toShort()
            }
        }
    }
}
