package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.data.AppPreferences
import com.example.data.ProtectedApp
import com.example.ui.components.FingerprintScanner
import com.example.viewmodel.BiometricViewModel

@Composable
fun AppLockScreen(
    viewModel: BiometricViewModel,
    preferences: AppPreferences
) {
    val coroutineScope = rememberCoroutineScope()
    val protectedApps by viewModel.protectedApps.collectAsState()
    val activelySimulatingApp by viewModel.activelySimulatingLockedApp.collectAsState()
    var isAppRevealedByLockSim by remember { mutableStateOf(false) }

    // State to add a custom app
    var showAddDialog by remember { mutableStateOf(false) }
    var newAppName by remember { mutableStateOf("") }
    var newAppCategory by remember { mutableStateOf("Custom") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. App Lock Master Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                    border = borderStrokeGlow()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MASTER BIOMETRIC SENTRY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "App Lock Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Switch(
                            checked = preferences.activeProtectionsEnabled,
                            onCheckedChange = { isChecked ->
                                viewModel.updatePreferences { it.copy(activeProtectionsEnabled = isChecked) }
                                viewModel.logUnlockAttempt(
                                    isSuccess = true,
                                    method = "System Sentry",
                                    details = "Master Lock Sentry set to ${if (isChecked) "ENABLED" else "DISABLED"}"
                                )
                            }
                        )
                    }
                }
            }

            // 2. Simulator explanation
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Simulate icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "INTERACTIVE VAULT TESTING",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap on any item with a checkmark to test-launch it and experience the premium biometric shield screen!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // 3. App list header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROTECTED APPLICATIONS",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )
                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Custom App",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add custom app", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Render protected app rows
            items(protectedApps) { app ->
                AppLockRow(
                    app = app,
                    isMasterEnabled = preferences.activeProtectionsEnabled,
                    onToggleEnabled = { viewModel.toggleAppProtection(app) },
                    onSimulateLaunch = {
                        isAppRevealedByLockSim = false
                        viewModel.triggerSimulatedAppLock(app)
                    }
                )
            }
        }

        // --- FULL SCREEN APP LOCK SHIELD SIMULATOR OVERLAY ---
        AnimatedVisibility(
            visible = activelySimulatingApp != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            activelySimulatingApp?.let { app ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isAppRevealedByLockSim) {
                        // Protected Vault Sentry
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Https,
                                contentDescription = "Sentry Lock",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "FINGERSTAMP APP LOCK",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = app.displayName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Highly Encrypted • Biometrics Required",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            // Interactive Fingerprint Scanner built in place
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White.copy(alpha = 0.03f))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                FingerprintScanner(
                                    preferences = preferences,
                                    onScanStart = {},
                                    onScanComplete = { isSuccess ->
                                        if (isSuccess) {
                                            isAppRevealedByLockSim = true
                                            viewModel.logUnlockAttempt(
                                                isSuccess = true,
                                                method = "App Vault Sentry",
                                                details = "Decrypted vault key and unlocked ${app.displayName} with authenticated biomatrices"
                                            )
                                        } else {
                                            viewModel.logUnlockAttempt(
                                                isSuccess = false,
                                                method = "App Vault Sentry",
                                                details = "Authentication mismatch while unlocking ${app.displayName}"
                                            )
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Touch & hold scanner to decrypt data vault",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            // PIN Fallback trigger
                            OutlinedButton(
                                onClick = {
                                    isAppRevealedByLockSim = true
                                    viewModel.logUnlockAttempt(
                                        isSuccess = true,
                                        method = "Device PIN/Pass",
                                        details = "Successfully bypassed App Vault for ${app.displayName} using fallback secure credentials"
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pin,
                                    contentDescription = "PIN Fallback",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Verify PIN / Pass fallback", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = { viewModel.triggerSimulatedAppLock(null) }) {
                                Text(text = "Cancel & Close Sentry", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else {
                        // App content Revealed! Show mock data perfectly
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(32.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .padding(24.dp)
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(app.iconId),
                                contentDescription = "App Icon Revealed",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "VAULT UNLOCKED",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = app.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = when (app.iconId) {
                                    "Gallery" -> "⚡ Mock Files: info_assets_2026.png, system_secret_key.enc, biometrics_core_debug.log, profile_backup.tar"
                                    "Messages" -> "💬 Messages: 'Code compiled correctly.' 'Meeting with Rahul Shah tomorrow.' 'Your OTP is 921045'."
                                    "Banking" -> "💳 Balance: \$14,920.45 USD • Account: **** 9210 • Sentry Status: Max Secured"
                                    "Social" -> "🌐 Connected to Decentralized Social Sphere matrix. Private messages encrypted."
                                    else -> "📂 Secure local sandbox files: db_replica_v1.sqlite, session_cookies.dat, biometric_tokens.json"
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.triggerSimulatedAppLock(null) },
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(text = "Re-Lock App & Exit", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- Dialog to add custom app ---
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text(text = "Add custom protected app", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = newAppName,
                            onValueChange = { newAppName = it },
                            label = { Text(text = "Application display name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Custom", "Banking", "Social").forEach { category ->
                                val selected = newAppCategory == category
                                Button(
                                    onClick = { newAppCategory = category },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(text = category, color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newAppName.isNotBlank()) {
                                coroutineScope.launch {
                                    viewModel.toggleAppProtection(
                                        ProtectedApp(
                                            packageName = "com.custom.${newAppName.lowercase().replace(" ", "")}",
                                            displayName = newAppName,
                                            iconId = newAppCategory,
                                            isProtected = true
                                        )
                                    )
                                }
                                newAppName = ""
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text(text = "Create Protection")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AppLockRow(
    app: ProtectedApp,
    isMasterEnabled: Boolean,
    onToggleEnabled: () -> Unit,
    onSimulateLaunch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = app.isProtected && isMasterEnabled) { onSimulateLaunch() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(app.iconId),
                    contentDescription = app.displayName,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (app.isProtected && isMasterEnabled) "Tap to launch secure container" else "App is secure on system",
                    fontSize = 11.sp,
                    color = if (app.isProtected && isMasterEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            Checkbox(
                checked = app.isProtected,
                onCheckedChange = { onToggleEnabled() }
            )
        }
    }
}

fun getCategoryIcon(categoryId: String) = when (categoryId) {
    "Gallery" -> Icons.Default.PhotoLibrary
    "Messages" -> Icons.Default.Sms
    "Banking" -> Icons.Default.AccountBalanceWallet
    "Social" -> Icons.Default.Public
    else -> Icons.Default.FolderZip
}

@Composable
fun borderStrokeGlow() = CardDefaults.outlinedCardBorder()
