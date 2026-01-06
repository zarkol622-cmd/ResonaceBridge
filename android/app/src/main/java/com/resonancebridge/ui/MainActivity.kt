
package com.resonancebridge.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.resonancebridge.audio.IsochronicGenerator
import com.resonancebridge.ui.theme.ResonanceBridgeTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResonanceBridgeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = { MainTopBar() },
        bottomBar = { MainBottomBar(viewModel) },
        floatingActionButton = { MainFAB(viewModel) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0A0E14)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Status Card
                StatusCard(viewModel)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Wave Visualization
                WaveVisualizer(viewModel)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Frequency Control
                FrequencyControl(viewModel)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Protocol Selection
                ProtocolSection(viewModel)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Harmonics Control
                HarmonicsControl(viewModel)
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {
    TopAppBar(
        title = {
            Text(
                "RESONANCE BRIDGE",
                color = Color(0xFF7C4DFF),
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0A0E14)
        ),
        actions = {
            IconButton(onClick = { /* Open settings */ }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* Open research data */ }) {
                Icon(
                    Icons.Default.Science,
                    contentDescription = "Research",
                    tint = Color(0xFF03DAC5)
                )
            }
        }
    )
}

@Composable
fun StatusCard(viewModel: MainViewModel) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentFrequency by viewModel.currentFrequency.collectAsState()
    val sessionTime by viewModel.sessionTime.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isPlaying) "СИНХРОНІЗАЦІЯ АКТИВНА" else "ГОТОВО ДО СИНХРОНІЗАЦІЇ",
                        color = if (isPlaying) Color(0xFF4CAF50) else Color(0xFFFFB74D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = if (isPlaying) "ЕФЕКТИВНІСТЬ: 87%" else "СТАН: СПОКІЙ",
                        color = Color(0xFFA0A0B0),
                        fontSize = 12.sp
                    )
                }
                
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (isPlaying) Color(0xFF4CAF50) else Color(0xFFFF6B6B),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoItem(
                    title = "ЧАСТОТА",
                    value = String.format("%.2f", currentFrequency),
                    unit = "Гц",
                    icon = Icons.Default.Waves
                )
                
                VerticalDivider(
                    color = Color(0xFF333344),
                    modifier = Modifier.height(40.dp)
                )
                
                InfoItem(
                    title = "ЧАС СЕАНСУ",
                    value = sessionTime.toString(),
                    unit = "хв",
                    icon = Icons.Default.Timer
                )
                
                VerticalDivider(
                    color = Color(0xFF333344),
                    modifier = Modifier.height(40.dp)
                )
                
                InfoItem(
                    title = "ГАРМОНІКИ",
                    value = if (viewModel.useHarmonics.value) "ON" else "OFF",
                    unit = "",
                    icon = Icons.Default.Tune
                )
            }
        }
    }
}

@Composable
fun InfoItem(title: String, value: String, unit: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = title,
            tint = Color(0xFF7C4DFF),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "$title $unit",
            color = Color(0xFFA0A0B0),
            fontSize = 12.sp
        )
    }
}

@Composable
fun WaveVisualizer(viewModel: MainViewModel) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentFrequency by viewModel.currentFrequency.collectAsState()
    val time by viewModel.waveTime.collectAsState()
    
    val animatedProgress by animateFloatAsState(
        targetValue = time,
        animationSpec = infiniteRepeatable(
            animation = tween(
                duration = (1000 / currentFrequency).toInt(),
                easing = LinearEasing
            )
        ),
        label = "wave_animation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151522)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background grid
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = Color(0xFF333344)
                val strokeWidth = 1f
                
                // Vertical lines
                for (x in 0..10) {
                    val xPos = size.width * x / 10
                    drawLine(
                        color = gridColor,
                        start = Offset(xPos, 0f),
                        end = Offset(xPos, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                
                // Horizontal lines
                for (y in 0..6) {
                    val yPos = size.height * y / 6
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, yPos),
                        end = Offset(size.width, yPos),
                        strokeWidth = strokeWidth
                    )
                }
            }
            
            // Main wave
            Canvas(modifier = Modifier.fillMaxSize()) {
                val waveColor = if (isPlaying) Color(0xFF7C4DFF) else Color(0xFF666677)
                val strokeWidth = 3f
                val amplitude = size.height * 0.3f
                val centerY = size.height / 2
                val points = mutableListOf<Offset>()
                
                for (x in 0..size.width.toInt()) {
                    val normalizedX = x.toFloat() / size.width
                    val time = normalizedX * 4 * Math.PI + animatedProgress * 2 * Math.PI
                    
                    // Base frequency
                    var y = sin(time.toFloat()).toDouble()
                    
                    // Add harmonics if enabled
                    if (viewModel.useHarmonics.value) {
                        val harmonic1 = sin(time * 2) * 0.3
                        val harmonic2 = sin(time * 3) * 0.2
                        y += harmonic1 + harmonic2
                    }
                    
                    val yPos = centerY + (y * amplitude).toFloat()
                    points.add(Offset(x.toFloat(), yPos))
                }
                
                // Draw wave line
                if (points.size >= 2) {
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = waveColor,
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = strokeWidth
                        )
                    }
                }
                
                // Draw frequency label
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${String.format("%.2f", currentFrequency)} Hz",
                        size.width * 0.05f,
                        size.height * 0.9f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 24f
                        }
                    )
                }
            }
            
            // Real-time frequency indicator
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    val indicatorColor = animateColorAsState(
                        targetValue = when {
                            currentFrequency in 7.0f..9.0f -> Color(0xFF4CAF50) // Alpha range
                            currentFrequency in 13.0f..15.0f -> Color(0xFF2196F3) // Low beta
                            currentFrequency in 19.0f..21.0f -> Color(0xFFFF9800) // High beta
                            else -> Color(0xFF7C4DFF)
                        },
                        label = "indicator_color"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = indicatorColor.value,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun FrequencyControl(viewModel: MainViewModel) {
    val currentFrequency by viewModel.currentFrequency.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "РЕГУЛЮВАННЯ ЧАСТОТИ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Frequency slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Δ",
                    color = Color(0xFFA0A0B0),
                    fontSize = 14.sp,
                    modifier = Modifier.width(32.dp)
                )
                
                Slider(
                    value = currentFrequency,
                    onValueChange = { viewModel.updateFrequency(it) },
                    valueRange = 1f..30f,
                    steps = 29,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF7C4DFF),
                        activeTrackColor = Color(0xFF7C4DFF),
                        inactiveTrackColor = Color(0xFF333344)
                    )
                )
                
                Text(
                    text = "θ",
                    color = Color(0xFFA0A0B0),
                    fontSize = 14.sp,
                    modifier = Modifier.width(32.dp)
                )
            }
            
            // Frequency labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FrequencyLabel("Дельта", "1-4 Гц", 2.5f, currentFrequency)
                FrequencyLabel("Тета", "4-8 Гц", 6f, currentFrequency)
                FrequencyLabel("Альфа", "8-14 Гц", 10f, currentFrequency)
                FrequencyLabel("Бета", "14-30 Гц", 22f, currentFrequency)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick frequency buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickFrequencyButton(
                    frequency = 1.5f,
                    label = "Глибокий сон",
                    viewModel = viewModel
                )
                
                QuickFrequencyButton(
                    frequency = 6f,
                    label = "Медитація",
                    viewModel = viewModel
                )
                
                QuickFrequencyButton(
                    frequency = 7.83f,
                    label = "Шумана",
                    viewModel = viewModel,
                    isHighlighted = true
                )
                
                QuickFrequencyButton(
                    frequency = 14f,
                    label = "Фокус",
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun FrequencyLabel(name: String, range: String, freq: Float, currentFreq: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name,
            color = if (currentFreq in freq-2f..freq+2f) Color(0xFF7C4DFF) else Color(0xFF666677),
            fontSize = 12.sp,
            fontWeight = if (currentFreq in freq-2f..freq+2f) FontWeight.Bold else FontWeight.Normal
        )
        
        Text(
            text = range,
            color = Color(0xFFA0A0B0),
            fontSize = 10.sp
        )
    }
}

@Composable
fun QuickFrequencyButton(
    frequency: Float,
    label: String,
    viewModel: MainViewModel,
    isHighlighted: Boolean = false
) {
    val buttonColor = if (isHighlighted) Color(0xFF7C4DFF) else Color(0xFF333344)
    
    Button(
        onClick = { viewModel.updateFrequency(frequency) },
        modifier = Modifier.width(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.1f", frequency),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp
            )
        }
    }
}

@Composable
fun ProtocolSection(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ПРОТОКОЛИ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Protocol cards
            LazyColumn {
                item {
                    ProtocolCard(
                        name = "Базова релаксація",
                        description = "10 хв синхронізації з 7.83 Гц",
                        duration = "10:00",
                        icon = Icons.Default.Spa,
                        onClick = {
                            viewModel.startProtocol(7.83f, 10)
                        }
                    )
                }
                
                item {
                    ProtocolCard(
                        name = "Глибока медитація",
                        description = "Тета-діапазон 6 Гц → Альфа 10 Гц",
                        duration = "20:00",
                        icon = Icons.Default.SelfImprovement,
                        onClick = {
                            viewModel.startProtocol(6f, 5)
                            // TODO: Add sequence
                        }
                    )
                }
                
                item {
                    ProtocolCard(
                        name = "Фокус та навчання",
                        description = "Бета-діапазон 14-16 Гц",
                        duration = "15:00",
                        icon = Icons.Default.School,
                        onClick = {
                            viewModel.startProtocol(14f, 15)
                        }
                    )
                }
                
                item {
                    ProtocolCard(
                        name = "Відновлення сну",
                        description = "Дельта 2.5 Гц → Тета 5 Гц",
                        duration = "30:00",
                        icon = Icons.Default.Nightlight,
                        onClick = {
                            viewModel.startProtocol(2.5f, 30)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProtocolCard(
    name: String,
    description: String,
    duration: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF252538)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = name,
                tint = Color(0xFF7C4DFF),
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = description,
                    color = Color(0xFFA0A0B0),
                    fontSize = 12.sp
                )
            }
            
            Badge(
                containerColor = Color(0xFF7C4DFF),
                contentColor = Color.White
            ) {
                Text(
                    text = duration,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HarmonicsControl(viewModel: MainViewModel) {
    val useHarmonics by viewModel.useHarmonics.collectAsState()
    val harmonicMix by viewModel.harmonicMix.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ГАРМОНІКИ ШУМАНА",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "14.3 Гц (β) • 20.8 Гц (β) • 27.3 Гц (γ)",
                        color = Color(0xFFA0A0B0),
                        fontSize = 12.sp
                    )
                }
                
                Switch(
                    checked = useHarmonics,
                    onCheckedChange = { viewModel.toggleHarmonics(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF7C4DFF),
                        checkedTrackColor = Color(0xFF7C4DFF).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color(0xFF666677),
                        uncheckedTrackColor = Color(0xFF333344)
                    )
                )
            }
            
            if (useHarmonics) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "ІНТЕНСИВНІСТЬ ГАРМОНІК",
                    color = Color(0xFFA0A0B0),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Slider(
                    value = harmonicMix,
                    onValueChange = { viewModel.updateHarmonicMix(it) },
                    valueRange = 0f..1f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF03DAC5),
                        activeTrackColor = Color(0xFF03DAC5),
                        inactiveTrackColor = Color(0xFF333344)
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Легкий вплив",
                        color = Color(0xFFA0A0B0),
                        fontSize = 12.sp
                    )
                    
                    Text(
                        text = "Помірний",
                        color = Color(0xFFA0A0B0),
                        fontSize = 12.sp
                    )
                    
                    Text(
                        text = "Інтенсивний",
                        color = Color(0xFFA0A0B0),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MainBottomBar(viewModel: MainViewModel) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    
    NavigationBar(
        containerColor = Color(0xFF0A0E14),
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { /* Open session history */ },
            icon = {
                Icon(
                    Icons.Default.History,
                    contentDescription = "History"
                )
            },
            label = { Text("Історія") }
        )
        
        NavigationBarItem(
            selected = false,
            onClick = { /* Open research */ },
            icon = {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = "Analytics"
                )
            },
            label = { Text("Аналітика") }
        )
        
        NavigationBarItem(
            selected = false,
            onClick = { /* Open community */ },
            icon = {
                Icon(
                    Icons.Default.Public,
                    contentDescription = "Community"
                )
            },
            label = { Text("Спільнота") }
        )
    }
}

@Composable
fun MainFAB(viewModel: MainViewModel) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val fabColor = if (isPlaying) Color(0xFFFF6B6B) else Color(0xFF7C4DFF)
    val icon = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow
    
    FloatingActionButton(
        onClick = {
            if (isPlaying) {
                viewModel.stopSession()
            } else {
                viewModel.startSession()
            }
        },
        containerColor = fabColor,
        contentColor = Color.White,
        modifier = Modifier.padding(bottom = 72.dp)
    ) {
        Icon(
            icon,
            contentDescription = if (isPlaying) "Stop" else "Start",
            modifier = Modifier.size(24.dp)
        )
    }
}