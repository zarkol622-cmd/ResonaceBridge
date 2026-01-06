package com.resonancebridge.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resonancebridge.audio.IsochronicGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.*

class MainViewModel(private val context: Context) : ViewModel() {
    private val generator = IsochronicGenerator(context)
    
    // State flows
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentFrequency = MutableStateFlow(7.83f)
    val currentFrequency: StateFlow<Float> = _currentFrequency.asStateFlow()
    
    private val _useHarmonics = MutableStateFlow(false)
    val useHarmonics: StateFlow<Boolean> = _useHarmonics.asStateFlow()
    
    private val _harmonicMix = MutableStateFlow(0.3f)
    val harmonicMix: StateFlow<Float> = _harmonicMix.asStateFlow()
    
    private val _sessionTime = MutableStateFlow(0)
    val sessionTime: StateFlow<Int> = _sessionTime.asStateFlow()
    
    private val _waveTime = MutableStateFlow(0f)
    val waveTime: StateFlow<Float> = _waveTime.asStateFlow()
    
    private var sessionTimer: Timer? = null
    private var waveTimer: Timer? = null
    
    init {
        startWaveAnimation()
    }
    
    fun startSession() {
        _isPlaying.value = true
        generator.start(
            IsochronicGenerator.Config(
                baseFrequency = _currentFrequency.value,
                useHarmonics = _useHarmonics.value,
                harmonicMix = _harmonicMix.value,
                volume = 0.5f
            )
        )
        
        startSessionTimer()
    }
    
    fun stopSession() {
        _isPlaying.value = false
        generator.stop()
        stopSessionTimer()
        _sessionTime.value = 0
    }
    
    fun updateFrequency(frequency: Float) {
        _currentFrequency.value = frequency
        if (_isPlaying.value) {
            generator.updateConfig(
                IsochronicGenerator.Config(
                    baseFrequency = frequency,
                    useHarmonics = _useHarmonics.value,
                    harmonicMix = _harmonicMix.value
                )
            )
        }
    }
    
    fun toggleHarmonics(enabled: Boolean) {
        _useHarmonics.value = enabled
        if (_isPlaying.value) {
            generator.updateConfig(
                IsochronicGenerator.Config(
                    baseFrequency = _currentFrequency.value,
                    useHarmonics = enabled,
                    harmonicMix = _harmonicMix.value
                )
            )
        }
    }
    
    fun updateHarmonicMix(mix: Float) {
        _harmonicMix.value = mix
        if (_isPlaying.value) {
            generator.updateConfig(
                IsochronicGenerator.Config(
                    baseFrequency = _currentFrequency.value,
                    useHarmonics = _useHarmonics.value,
                    harmonicMix = mix
                )
            )
        }
    }
    
    fun startProtocol(frequency: Float, durationMinutes: Int) {
        updateFrequency(frequency)
        if (!_isPlaying.value) {
            startSession()
        }
        // TODO: Implement protocol timer
    }
    
    private fun startSessionTimer() {
        sessionTimer?.cancel()
        sessionTimer = Timer()
        sessionTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                _sessionTime.value += 1
            }
        }, 60000, 60000) // Update every minute
    }
    
    private fun stopSessionTimer() {
        sessionTimer?.cancel()
        sessionTimer = null
    }
    
    private fun startWaveAnimation() {
        waveTimer?.cancel()
        waveTimer = Timer()
        waveTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                _waveTime.value = (_waveTime.value + 0.02f) % 1f
            }
        }, 16, 16) // ~60 FPS
    }
    
    override fun onCleared() {
        super.onCleared()
        sessionTimer?.cancel()
        waveTimer?.cancel()
        generator.stop()
    }
}