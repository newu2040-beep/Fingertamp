package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.data.AppPreferences
import com.example.ui.theme.FingerThemeColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// Representation of particles spawned during the scan
data class ScanParticle(
    val id: Int,
    val offset: Offset,
    val velocity: Offset,
    val color: Color,
    val size: Float,
    val alpha: Float,
    val maxAge: Float,
    val age: Float = 0f
)

@Composable
fun FingerprintScanner(
    preferences: AppPreferences,
    modifier: Modifier = Modifier,
    isAuthenticating: Boolean = false,
    onScanStart: () -> Unit = {},
    onScanComplete: (Boolean) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current

    // Touch status
    var isPressed by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableStateOf(0f) }
    var particles by remember { mutableStateOf(listOf<ScanParticle>()) }
    var scanSuccessState by remember { mutableStateOf<Boolean?>(null) } // null: none, true: success, false: failed

    // Fetch theme colors based on style
    val themeChoice = preferences.fingerprintStyle
    val styleColors = FingerThemeColors.ThemeColorsMap[themeChoice] ?: listOf(Color(0xFF00E5FF), Color(0xFF00838F))
    val primaryColor = styleColors.first()
    val secondaryColor = if (styleColors.size > 1) styleColors[1] else primaryColor
    val glowIntensity = preferences.glowIntensity
    val animationSpeed = preferences.animationSpeed

    // 1. Infinite transition for ambient states (e.g. glowing pulse or scanner lines)
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    
    // Pulse animation for ambient glow
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween((1200 / animationSpeed).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Scanner line position (vertical scan line)
    val scannerLineOffset by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween((2000 / animationSpeed).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner"
    )

    // Hologram rotative angle
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween((6000 / animationSpeed).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // App preferences custom scanner size
    val sizeDp = (140 * preferences.scannerSize).dp

    // Simulation delay effect
    LaunchedEffect(isPressed) {
        if (isPressed) {
            scanSuccessState = null
            onScanStart()
            if (preferences.touchFeedbackEnabled) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
            
            // Increment progress
            val duration = (1500 / animationSpeed).toLong()
            val steps = 30
            val stepDelay = duration / steps
            for (i in 1..steps) {
                if (!isPressed) break
                delay(stepDelay)
                scanProgress = i.toFloat() / steps
                
                // Spawn particles continuously on trace center
                if (preferences.unlockAnimation == "Particle Burst" || preferences.unlockAnimation == "Liquid Glow") {
                    val pCount = (2..5).random()
                    val newParticles = (1..pCount).map {
                        ScanParticle(
                            id = (0..100000).random(),
                            offset = Offset(0f, 0f), // relativized to center later
                            velocity = Offset((-15..15).random().toFloat(), (-15..15).random().toFloat()),
                            color = listOf(primaryColor, secondaryColor, Color.White).random().copy(alpha = 0.9f),
                            size = (4..12).random().toFloat(),
                            alpha = 1f,
                            maxAge = (20..40).random().toFloat()
                        )
                    }
                    particles = (particles + newParticles).take(40)
                }
            }

            if (isPressed) {
                // Done scanning! Determine success or failure (90% success rate simulated or passed parameter)
                if (preferences.touchFeedbackEnabled) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
                val success = (0..10).random() > 1 // 90% success rate
                scanSuccessState = success
                onScanComplete(success)
                
                // Particle blast if success
                if (success) {
                    val burstCount = 20
                    particles = (1..burstCount).map {
                        ScanParticle(
                            id = (0..100000).random(),
                            offset = Offset(0f, 0f),
                            velocity = Offset(
                                (sin(it.toDouble()) * (10..25).random()).toFloat(),
                                (cos(it.toDouble()) * (10..25).random()).toFloat()
                            ),
                            color = primaryColor,
                            size = (6..16).random().toFloat(),
                            alpha = 1f,
                            maxAge = 50f
                        )
                    }
                }
                delay(600)
                isPressed = false
                scanProgress = 0f
            }
        } else {
            scanProgress = 0f
        }
    }

    // Particle ticking
    LaunchedEffect(particles) {
        if (particles.isNotEmpty()) {
            delay(16)
            particles = particles.mapNotNull { p ->
                if (p.age >= p.maxAge) null
                else p.copy(
                    offset = p.offset + p.velocity,
                    age = p.age + 1f,
                    alpha = 1f - (p.age / p.maxAge),
                    size = p.size * 0.95f
                )
            }
        }
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2, height / 2)
            val maxRadius = width.coerceAtMost(height) / 2

            // Colors based on state
            val activeColor = when (scanSuccessState) {
                true -> Color(0xFF34C759) // Success Animation Color
                false -> Color(0xFFFF3B30) // Failure Animation Color
                else -> if (isPressed) primaryColor else primaryColor.copy(alpha = 0.7f)
            }

            val accentColor = when (scanSuccessState) {
                true -> Color(0xFFE5FFE5)
                false -> Color(0xFFFFE5E5)
                else -> secondaryColor
            }

            // A) Scanner Glow Effects (Underneath)
            val shadowColor = activeColor.copy(alpha = 0.25f * glowIntensity * ambientPulse)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(activeColor.copy(alpha = 0.4f * glowIntensity), Color.Transparent),
                    center = center,
                    radius = maxRadius * 1.2f
                ),
                radius = maxRadius * 1.2f
            )

            // Dynamic scan boundary circle
            drawCircle(
                color = activeColor.copy(alpha = 0.2f),
                radius = maxRadius * 0.9f,
                style = Stroke(width = 2f)
            )

            // B) Draw custom fingerprint biometric ridges
            val ridgeColor = activeColor
            val scaleFactor = 0.8f
            val baseRadius = maxRadius * scaleFactor

            // Draw concentric curved ridges
            for (i in 1..5) {
                val r = baseRadius * (i / 5.0f)
                val strokeWidth = 3.5f + (i * 0.5f)
                
                // Add biometric gaps to represent ridges
                withTransform({
                    rotate(degrees = i * 25f + (if (isPressed) scanProgress * 15f else 0f), pivot = center)
                }) {
                    drawArc(
                        color = ridgeColor,
                        startAngle = 30f + (i * 10f),
                        sweepAngle = 100f + (i * 15f),
                        useCenter = false,
                        topLeft = Offset(center.x - r, center.y - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    
                    drawArc(
                        color = ridgeColor,
                        startAngle = 180f + (i * 8f),
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(center.x - r, center.y - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // Draw center whorl
            drawArc(
                color = ridgeColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - baseRadius * 0.1f, center.y - baseRadius * 0.1f),
                size = Size(baseRadius * 0.2f, baseRadius * 0.2f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            // C) Custom scanner category-specific overlay effects
            when (preferences.unlockAnimation) {
                "Neon Scanner" -> {
                    // sweep laser line over fingerprint
                    val lineY = height * scannerLineOffset
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, accentColor, accentColor, Color.Transparent)
                        ),
                        start = Offset(width * 0.15f, lineY),
                        end = Offset(width * 0.85f, lineY),
                        strokeWidth = 6f
                    )
                    // Scanner glow underneath laser
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(accentColor.copy(alpha = 0.2f), Color.Transparent),
                            startY = lineY,
                            endY = lineY + 30f
                        ),
                        topLeft = Offset(width * 0.15f, lineY),
                        size = Size(width * 0.7f, 30f)
                    )
                }

                "Cyber Grid" -> {
                    // Futuristic grid dots & radar line
                    val progressDegree = rotationAngle
                    withTransform({
                        rotate(degrees = progressDegree, pivot = center)
                    }) {
                        // radar sweep line
                        drawLine(
                            color = accentColor.copy(alpha = 0.6f),
                            start = center,
                            end = Offset(center.x + maxRadius * 0.9f, center.y),
                            strokeWidth = 3f
                        )
                        // sweeping aura
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(accentColor.copy(alpha = 0.3f), Color.Transparent),
                                center = center
                            ),
                            startAngle = -20f,
                            sweepAngle = 45f,
                            useCenter = true
                        )
                    }
                    // Grid intersection lines
                    for (xIndex in 2..8) {
                        val posX = width * (xIndex / 10f)
                        drawLine(
                            color = activeColor.copy(alpha = 0.08f),
                            start = Offset(posX, height * 0.1f),
                            end = Offset(posX, height * 0.9f),
                            strokeWidth = 1f
                        )
                    }
                }

                "Pulse Wave" -> {
                    // Concentric expanding ring lines
                    val waveProgress = (scannerLineOffset * 2f) % 1f
                    drawCircle(
                        color = accentColor.copy(alpha = 0.6f * (1f - waveProgress)),
                        radius = maxRadius * 0.9f * waveProgress,
                        style = Stroke(width = 4f)
                    )
                }

                "Matrix Scan" -> {
                    // Matrix code/particle vertical lines
                    val pulse = scannerLineOffset
                    for (seg in 1..6) {
                        val segX = width * (0.21f + (seg * 0.11f))
                        val startY = height * ((pulse + (seg * 0.15f)) % 0.8f + 0.1f)
                        drawLine(
                            brush = Brush.verticalGradient(
                                colors = listOf(activeColor, Color.Transparent),
                                startY = startY,
                                endY = startY + 40f
                            ),
                            start = Offset(segX, startY),
                            end = Offset(segX, startY + 40f),
                            strokeWidth = 4f
                        )
                    }
                }

                "Liquid Glow" -> {
                    // Blobs / glowing overlay
                    val angle1 = rotationAngle * (Math.PI / 180f)
                    val offset1 = Offset(center.x + sin(angle1).toFloat() * 15f, center.y + cos(angle1).toFloat() * 10f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent),
                            center = offset1,
                            radius = maxRadius * 0.5f
                        ),
                        center = offset1,
                        radius = maxRadius * 0.5f
                    )
                }

                "Hologram Scan" -> {
                    // Floating tech brackets & rotation lines
                    withTransform({
                        rotate(degrees = rotationAngle, pivot = center)
                    }) {
                        drawCircle(
                            color = accentColor,
                            radius = maxRadius * 0.88f,
                            style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 25f), 0f))
                        )
                    }
                    withTransform({
                        rotate(degrees = -rotationAngle * 1.5f, pivot = center)
                    }) {
                        drawCircle(
                            color = activeColor,
                            radius = maxRadius * 0.78f,
                            style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 40f), 0f))
                        )
                    }
                    // Corners
                    val scope = maxRadius * 0.95f
                    drawArc(activeColor, -15f, 30f, false, center - Offset(scope, scope), Size(scope*2, scope*2), style = Stroke(5f))
                    drawArc(activeColor, 165f, 30f, false, center - Offset(scope, scope), Size(scope*2, scope*2), style = Stroke(5f))
                }

                "Energy Ring" -> {
                    // Neon energy path around target
                    withTransform({
                        rotate(rotationAngle * 2, pivot = center)
                    }) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(accentColor, activeColor, Color.Transparent, accentColor)
                            ),
                            radius = maxRadius * 0.92f,
                            style = Stroke(width = 4f)
                        )
                    }
                }

                "Minimal Modern" -> {
                    // Simple clean sleek expanding target dot
                    drawCircle(
                        color = accentColor.copy(alpha = 0.2f),
                        radius = maxRadius * (0.8f + ambientPulse * 0.1f),
                        style = Stroke(1.5.dp.toPx())
                    )
                }
            }

            // D) Draw Touch Scanning overlay indicators
            if (isPressed) {
                // Progress sweeping arc
                drawArc(
                    color = accentColor,
                    startAngle = -90f,
                    sweepAngle = 360f * scanProgress,
                    useCenter = false,
                    topLeft = Offset(center.x - maxRadius * 0.91f, center.y - maxRadius * 0.91f),
                    size = Size(maxRadius * 1.82f, maxRadius * 1.82f),
                    style = Stroke(width = 5f)
                )

                // Fill color pulse percentage
                drawCircle(
                    color = activeColor.copy(alpha = 0.12f * scanProgress),
                    radius = maxRadius * 0.9f
                )
            }

            // E) Render visual feedback particles
            particles.forEach { p ->
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size,
                    center = center + p.offset
                )
            }
        }
    }
}

// Extension to offset box centers for drawing arcs nicely
private operator fun Offset.minus(other: Offset): Offset {
    return Offset(x - other.x, y - other.y)
}
