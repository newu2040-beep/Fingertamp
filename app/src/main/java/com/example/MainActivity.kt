package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.AppPreferences
import com.example.security.BiometricPromptHelper
import com.example.ui.screens.*
import com.example.ui.theme.FingerThemeColors
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BiometricViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val biometricViewModel: BiometricViewModel = viewModel()
            val preferences by biometricViewModel.preferences.collectAsState()
            
            val isDarkTheme = when (preferences.themeMode) {
                "Light" -> false
                "Dark", "AMOLED" -> true
                else -> isSystemInDarkTheme()
            }

            // Generate premium dynamic scheme based on client-configured selections in database
            val activeScheme = FingerThemeColors.getColorSchemeForPremium(
                themeName = preferences.premiumTheme,
                isDark = isDarkTheme
            )

            MaterialTheme(
                colorScheme = activeScheme,
                typography = MaterialTheme.typography
            ) {
                // Background gradient brush
                val ambientBrush = FingerThemeColors.getAmbientBackgroundBrush(
                    themeName = preferences.premiumTheme,
                    isDark = isDarkTheme
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ambientBrush)
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    FingerStampApp(
                        viewModel = biometricViewModel,
                        preferences = preferences,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FingerStampApp(
    viewModel: BiometricViewModel,
    preferences: AppPreferences,
    isDarkTheme: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    // Show theme selector sheets
    var showThemeSheet by remember { mutableStateOf(false) }

    val tabs = listOf(
        NavigationTab("dashboard", "Dashboard", Icons.Default.Dashboard),
        NavigationTab("studio", "Studio", Icons.Default.Tune),
        NavigationTab("applock", "App Lock", Icons.Default.Lock),
        NavigationTab("lockdesigner", "Lock Screen", Icons.Default.EdgesensorHigh),
        NavigationTab("animations", "Anim Lab", Icons.Default.Science)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent, // fully dynamic gradient bleeding
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "FingerStamp",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 19.sp,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "BIOMETRIC STUDIO",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    letterSpacing = 1.8.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Pulsing emerald indicator from Immersive UI
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse_dot_trans")
                            val pulseAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.4f,
                                targetValue = 1.0f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = LinearOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "pulse_dot_alpha"
                            )
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B)) // bg-slate-800
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34D399).copy(alpha = pulseAlpha)) // bg-emerald-400
                                )
                            }

                            // Theme customizer launcher
                            IconButton(onClick = { showThemeSheet = true }) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Theme Palette Selector",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.80f),
                tonalElevation = 8.dp
            ) {
                tabs.forEach { tab ->
                    val isSelected = currentRoute == tab.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != tab.route) {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("dashboard") {
                    DashboardScreen(
                        viewModel = viewModel,
                        preferences = preferences,
                        onNavigateToStudio = { navController.navigate("studio") },
                        onNavigateToAppLock = { navController.navigate("applock") }
                    )
                }
                composable("studio") {
                    ExperienceStudioScreen(viewModel = viewModel, preferences = preferences)
                }
                composable("applock") {
                    AppLockScreen(viewModel = viewModel, preferences = preferences)
                }
                composable("lockdesigner") {
                    LockScreenDesigner(viewModel = viewModel, preferences = preferences)
                }
                composable("animations") {
                    AnimationLibraryScreen(viewModel = viewModel, preferences = preferences)
                }
            }
        }

        // --- Bottom Sheet Customizer for Light, Dark, AMOLED & Premium Themes ---
        if (showThemeSheet) {
            ModalBottomSheet(
                onDismissRequest = { showThemeSheet = false },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "THEME MODES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Light", "Dark", "AMOLED").forEach { mode ->
                            val selected = preferences.themeMode == mode
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.updatePreferences { it.copy(themeMode = mode) }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = mode,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                    Text(
                        text = "PREMIUM BIOMETRIC THEMES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    val premiumThemes = listOf("Cyber Blue", "Aurora Purple", "Graphite Black", "Emerald Glass", "Titanium Silver", "Sunset Gold")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        premiumThemes.forEach { term ->
                            val selected = preferences.premiumTheme == term
                            val chipColor = when (term) {
                                "Cyber Blue" -> Color(0xFF00E5FF)
                                "Aurora Purple" -> Color(0xFFBF5AF2)
                                "Graphite Black" -> Color(0xFFE5E5EA)
                                "Emerald Glass" -> Color(0xFF34C759)
                                "Titanium Silver" -> Color(0xFF8E8E93)
                                "Sunset Gold" -> Color(0xFFFFCC00)
                                else -> MaterialTheme.colorScheme.primary
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                    )
                                    .border(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        viewModel.updatePreferences { it.copy(premiumTheme = term) }
                                        viewModel.logUnlockAttempt(
                                            isSuccess = true,
                                            method = "UI Customization",
                                            details = "Loaded Premium visual theme palette: $term"
                                        )
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(chipColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = term,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showThemeSheet = false },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Apply Aesthetics", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class NavigationTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@androidx.compose.runtime.Composable
fun Greeting(name: String, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}
