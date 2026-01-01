package com.resonancebridge

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.resonancebridge.core.IsochronicGenerator

class MainActivity : AppCompatActivity() {
    private lateinit var generator: IsochronicGenerator
    private lateinit var btnStartStop: Button
    private lateinit var tvStatus: TextView
    private lateinit var seekBarVolume: SeekBar
    private lateinit var tvWarning: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        generator = IsochronicGenerator()
        
        btnStartStop = findViewById(R.id.btn_start_stop)
        tvStatus = findViewById(R.id.tv_status)
        seekBarVolume = findViewById(R.id.seekbar_volume)
        tvWarning = findViewById(R.id.tv_warning)
        
        // Попередження
        tvWarning.text = """
            ⚠️ УВАГА:
            • Не використовуйте при епілепсії
            • Не використовуйте за кермом
            • Прийміть зручну позу
            • Починайте з низької гучності
        """.trimIndent()
        
        // Кнопка старт/стоп
        btnStartStop.setOnClickListener {
            if (generator.isPlaying()) {
                generator.stop()
                btnStartStop.text = "СТАРТ СИНХРОНІЗАЦІЇ"
                tvStatus.text = "Статус: зупинено"
            } else {
                val volume = seekBarVolume.progress / 100.0f
                generator.startIsochronic(volume = volume)
                btnStartStop.text = "СТОП"
                tvStatus.text = "Статус: синхронізація з 7.83 Гц"
            }
        }
        
        // Гучність
        seekBarVolume.progress = 30
        seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Майбутнє: регулювання гучності в реальному часі
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    override fun onDestroy() {
        generator.stop()
        super.onDestroy()
    }
}
