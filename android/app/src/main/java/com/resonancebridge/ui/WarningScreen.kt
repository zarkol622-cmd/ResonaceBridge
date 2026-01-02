package com.resonancebridge.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.resonancebridge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningScreen(navController: NavController) {
    var acceptedTerms by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "RESONANCE BRIDGE",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0E14)
                )
            )
        },
        containerColor = Color(0xFF0A0E14)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Warning Icon
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFFF6B6B),
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Main Warning
            Text(
                text = "КРИТИЧНО ВАЖЛИВЕ ПОПЕРЕДЖЕННЯ",
                color = Color(0xFFFF6B6B),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Risk List
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E2E)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RiskItem("Епілепсія та нападоподібні стани")
                    RiskItem("Вагітність")
                    RiskItem("Електронні імпланти (кардіостимулятор тощо)")
                    RiskItem("Психічні розлади (шизофренія, біполярний розлад)")
                    RiskItem("Вживання психоактивних речовин")
                    RiskItem("Неповноліття без нагляду дорослого")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // How it works
            Text(
                text = "ЯК ЦЕ ПРАЦЮЄ",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Додаток генерує звукові частоти (7.83 Гц та гармоніки) " +
                       "для синхронізації мозкових хвиль. Це може викликати:",
                color = Color(0xFFA0A0B0),
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            BulletPoint("Глибоке розслаблення або змінені стани свідомості")
            BulletPoint("Візуальні ефекти на закритих повіках")
            BulletPoint("Тимчасову дезорієнтацію після сесії")
            BulletPoint("Зміни у сні та концентрації")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Privacy Notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "КОНФІДЕНЦІЙНІСТЬ",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "• Всі дані обробляються локально на вашому пристрої\n" +
                               "• Мікрофон/камера використовуються ТІЛЬКИ за вашою згодою\n" +
                               "• Анонімні дані для досліджень збираються ТІЛЬКИ за додатковою згодою",
                        color = Color(0xFFA0A0B0),
                        lineHeight = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Terms Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = Color(0xFF666666)
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Я прочитав та розумію всі ризики. Я відповідаю за своє здоров'я.",
                    color = if (acceptedTerms) Color(0xFF4CAF50) else Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Exit Button
                OutlinedButton(
                    onClick = { /* Close app */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Text("ВИХІД")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Accept Button (disabled until terms accepted)
                Button(
                    onClick = { navController.navigate("main") },
                    modifier = Modifier.weight(1f),
                    enabled = acceptedTerms,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (acceptedTerms) 
                            MaterialTheme.colorScheme.primary 
                        else Color(0xFF666666),
                        disabledContainerColor = Color(0xFF666666)
                    )
                ) {
                    Text("ПРИЙМАЮ РИЗИК")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Version info
            Text(
                text = "Resonance Bridge v0.1.0-alpha\nEthical Neurotechnology • Open Source",
                color = Color(0xFF666666),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RiskItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = Color(0xFFFF6B6B),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color(0xFFCCCCCC),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 2.dp, end = 8.dp)
        )
        Text(
            text = text,
            color = Color(0xFFA0A0B0),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
