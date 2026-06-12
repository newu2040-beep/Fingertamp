package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferences
import com.example.ui.components.FingerprintScanner
import com.example.viewmodel.BiometricViewModel
import java.util.*

@Composable
fun LockScreenDesigner(
    viewModel: BiometricViewModel,
    preferences: AppPreferences
) {
    val clockStyles = listOf("Digital Glow", "Hologram Space", "Military Sector", "Matrix Grid", "Thin Minimal")
    val dateStyles = listOf("Minimal", "Abbreviated Cyber", "Full Standard")
    
    // Lock screen customization local triggers
    var showLockedScreenSim by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Title Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "LOCK SCREEN PERSONALIZATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Biometric Lock Screen Designer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Align widgets, pick clock styles, and mock-launch your custom security landing page.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // 2. Launch simulator call-to-action
            item {
                Button(
                    onClick = { showLockedScreenSim = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = "Launch Lock",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LAUNCH IMMERSIVE LOCK SCREEN PREVIEW",
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            // 3. Clock Style card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Futuristic Clock Styles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            clockStyles.forEach { style ->
                                val isSelected = preferences.clockStyle == style
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            viewModel.updatePreferences { it.copy(clockStyle = style) }
                                        }
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = style,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Date Styles Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Date Format Layouts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            dateStyles.forEach { dateStyle ->
                                val isSelected = preferences.dateStyle == dateStyle
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            viewModel.updatePreferences { it.copy(dateStyle = dateStyle) }
                                        }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dateStyle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Lock screen widgets and switches
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Interactive Security Widgets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Clock Widget option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Fingerprint-Inspired Ring Widget",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Renders concentric biometric safety ring on lock screen.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            Switch(
                                checked = preferences.privacyModeEnabled,
                                onCheckedChange = { isChecked ->
                                    viewModel.updatePreferences { it.copy(privacyModeEnabled = isChecked) }
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- FULL COVER LOCKSCREEN IMMERSIVE PREVIEW OVERLAY ---
        AnimatedVisibility(
            visible = showLockedScreenSim,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.background,
                                Color(0xFF070B14)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                // Outer physical layout framework
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top: Date + Personalized Futuristic Clock Styles
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        // Date style renderer
                        val calendar = Calendar.getInstance()
                        val mDateText = when (preferences.dateStyle) {
                            "Minimal" -> DateFormat.format("E, MMM d", calendar).toString()
                            "Abbreviated Cyber" -> "STAMP SYS_SYS // " + DateFormat.format("dd.MM.yy", calendar).toString()
                            else -> DateFormat.format("EEEE, d MMMM yyyy", calendar).toString()
                        }
                        Text(
                            text = mDateText.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Clock style renderer
                        val mTimeHour = DateFormat.format("HH", calendar).toString()
                        val mTimeMin = DateFormat.format("mm", calendar).toString()

                        when (preferences.clockStyle) {
                            "Digital Glow" -> {
                                Text(
                                    text = "$mTimeHour:$mTimeMin",
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            "Hologram Space" -> {
                                Text(
                                    text = "$mTimeHour\n$mTimeMin",
                                    fontSize = 62.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 52.sp,
                                    color = Color.White
                                )
                            }
                            "Military Sector" -> {
                                Text(
                                    text = "SEC $mTimeHour$mTimeMin.A8",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            "Matrix Grid" -> {
                                Text(
                                    text = "[$mTimeHour : $mTimeMin]",
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Green,
                                    letterSpacing = 4.sp
                                )
                            }
                            else -> { // Thin Minimal
                                Text(
                                    text = "$mTimeHour:$mTimeMin",
                                    fontSize = 80.sp,
                                    fontWeight = FontWeight.ExtraLight,
                                    fontFamily = FontFamily.SansSerif,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        // Ring Gauge widget if enabled
                        if (preferences.privacyModeEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Canvas(modifier = Modifier.size(50.dp)) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.1f),
                                    style = Stroke(width = 3.dp.toPx())
                                )
                                drawArc(
                                    color = CyberBluePrimary(),
                                    startAngle = -90f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }
                        }
                    }

                    // Middle: Ambient Mock Notifications
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sms,
                                        contentDescription = "Notif Message",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Rahul Shah • 2m ago",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Your fingerprint custom theme compiles optimally!",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom: Fingerprint Scanner & Quick Buttons
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 50.dp)
                    ) {
                        FingerprintScanner(
                            preferences = preferences,
                            onScanStart = {},
                            onScanComplete = { success ->
                                if (success) {
                                    showLockedScreenSim = false
                                    viewModel.logUnlockAttempt(
                                        isSuccess = true,
                                        method = "Lock Screen",
                                        details = "Personalized secure lockscreen successfully decrypted"
                                    )
                                } else {
                                    viewModel.logUnlockAttempt(
                                        isSuccess = false,
                                        method = "Lock Screen",
                                        details = "Lockscreen scan mismatch using ${preferences.fingerprintStyle} style"
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "HOLD CUSTOM BIOMETRIC TO UNLOCK",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Quick action symbols row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.FlashlightOn, contentDescription = "Flashlight", tint = Color.White)
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
