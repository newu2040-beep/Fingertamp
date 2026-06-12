package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferences
import com.example.ui.components.FingerprintScanner
import com.example.viewmodel.BiometricViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnimationLibraryScreen(
    viewModel: BiometricViewModel,
    preferences: AppPreferences
) {
    val categories = listOf(
        "Classic Fingerprint", "Neon Scanner", "Cyber Grid", "Pulse Wave",
        "Matrix Scan", "Liquid Glow", "Particle Burst", "Hologram Scan",
        "Energy Ring", "Minimal Modern"
    )

    // Selection state inside the library screen
    var selectedCategory by remember { mutableStateOf("Neon Scanner") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 1. Interactive Testing Lab Pad
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Test Lab",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SANDBOX PHYSICS LABORATORY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = selectedCategory,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Press and hold scanner below to test this engineering wave",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Temporarily override preferences' chosen unlockAnimation with the selected category for live sandbox testing!
                    val sandboxPrefs = preferences.copy(unlockAnimation = selectedCategory)
                    FingerprintScanner(
                        preferences = sandboxPrefs,
                        onScanStart = {},
                        onScanComplete = { isSuccess ->
                            viewModel.logUnlockAttempt(
                                isSuccess = isSuccess,
                                method = "Sandbox Physics Lab",
                                details = "Executed physical trace scan of $selectedCategory biometric wave (${if (isSuccess) "Aligned" else "Error"})"
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action to apply to app preferences permanently
                Button(
                    onClick = {
                        viewModel.updatePreferences { it.copy(unlockAnimation = selectedCategory) }
                        viewModel.logUnlockAttempt(
                            isSuccess = true,
                            method = "Personalization",
                            details = "Set permanent system lock transition style to: $selectedCategory"
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (preferences.unlockAnimation == selectedCategory) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Apply Icon",
                        tint = if (preferences.unlockAnimation == selectedCategory) MaterialTheme.colorScheme.onSurface
                        else Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (preferences.unlockAnimation == selectedCategory) "Active system default"
                        else "Apply as system default style",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (preferences.unlockAnimation == selectedCategory) MaterialTheme.colorScheme.onSurface
                        else Color.Black
                    )
                }
            }
        }

        // 2. Descriptive Library Categories Grid
        Text(
            text = "SELECT ANIMATIVE PHYSICS WAVE",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedCategory = category }
                        .border(
                            width = if (isSelected) 1.dp else 0.4.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (category) {
                                "Classic Fingerprint" -> "Traditional ridge pulse trace mapping"
                                "Neon Scanner" -> "Horizontal glowing laser line sweep"
                                "Cyber Grid" -> "Rotational sweep radar with grid dots"
                                "Pulse Wave" -> "Expanding concentric soundwave circular arcs"
                                "Matrix Scan" -> "Cascading binary vertical energy drops"
                                "Liquid Glow" -> "Translucent organic morphing liquid aura"
                                "Particle Burst" -> "Centrifugal particle sparks of security"
                                "Hologram Scan" -> "Rotating double HUD bracket circles"
                                "Energy Ring" -> "Swirling plasma neon color brush ring"
                                else -> "Clean minimal subtle outline glow"
                            },
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }
    }
}
