package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "app_preferences")
data class AppPreferences(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "Dark", // Light, Dark, AMOLED
    val premiumTheme: String = "Cyber Blue", // Cyber Blue, Aurora Purple, Graphite Black, Emerald Glass, Titanium Silver, Sunset Gold, None
    val fingerprintStyle: String = "Neon Blue", // Themes: Neon Blue, Emerald Green, Cyber Purple, Gold Glow, Crimson Red, Arctic White, Ocean Cyan, Sunset Orange, Graphite Black, Aurora Gradient
    val scannerSize: Float = 1.0f,
    val scannerPosition: Float = 0.0f, // vertical offset percentage or slider
    val animationSpeed: Float = 1.0f,
    val glowIntensity: Float = 0.8f,
    val touchFeedbackEnabled: Boolean = true,
    val unlockAnimation: String = "Neon Scanner", // Categories: Classic Fingerprint, Neon Scanner, Cyber Grid, Pulse Wave, Matrix Scan, Liquid Glow, Particle Burst, Hologram Scan, Energy Ring, Minimal Modern
    val successAnimation: String = "Particle Burst",
    val failureAnimation: String = "Pulse Wave",
    val clockStyle: String = "Digital Glow", // Futuristic Clock styles
    val dateStyle: String = "Abbreviated Cyber",
    val autoLockTimerSec: Int = 10,
    val privacyModeEnabled: Boolean = false,
    val activeProtectionsEnabled: Boolean = true
)

@Entity(tableName = "protected_apps")
data class ProtectedApp(
    @PrimaryKey val packageName: String,
    val displayName: String,
    val iconId: String, // Gallery, Messages, Banking, Social, Custom
    val isProtected: Boolean
)

@Entity(tableName = "unlock_logs")
data class UnlockLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean,
    val method: String, // Fingerprint, Face, PIN, Password
    val details: String
)

@Dao
interface BiometricDao {
    @Query("SELECT * FROM app_preferences WHERE id = 1 LIMIT 1")
    fun getPreferencesFlow(): Flow<AppPreferences?>

    @Query("SELECT * FROM app_preferences WHERE id = 1 LIMIT 1")
    suspend fun getPreferences(): AppPreferences?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preferences: AppPreferences)

    @Query("SELECT * FROM protected_apps")
    fun getProtectedAppsFlow(): Flow<List<ProtectedApp>>

    @Query("SELECT * FROM protected_apps")
    suspend fun getProtectedApps(): List<ProtectedApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProtectedApp(app: ProtectedApp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProtectedApps(apps: List<ProtectedApp>)

    @Query("SELECT * FROM unlock_logs ORDER BY timestamp DESC LIMIT 50")
    fun getUnlockLogsFlow(): Flow<List<UnlockLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnlockLog(log: UnlockLog)

    @Query("DELETE FROM unlock_logs")
    suspend fun clearUnlockLogs()
}

@Database(entities = [AppPreferences::class, ProtectedApp::class, UnlockLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun biometricDao(): BiometricDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "biometric_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class BiometricRepository(private val dao: BiometricDao) {
    val preferencesFlow: Flow<AppPreferences?> = dao.getPreferencesFlow()
    val protectedAppsFlow: Flow<List<ProtectedApp>> = dao.getProtectedAppsFlow()
    val unlockLogsFlow: Flow<List<UnlockLog>> = dao.getUnlockLogsFlow()

    suspend fun getPreferences(): AppPreferences {
        return dao.getPreferences() ?: AppPreferences().also {
            dao.savePreferences(it)
        }
    }

    suspend fun savePreferences(preferences: AppPreferences) {
        dao.savePreferences(preferences)
    }

    suspend fun getProtectedApps(): List<ProtectedApp> {
        val apps = dao.getProtectedApps()
        if (apps.isEmpty()) {
            val defaultApps = listOf(
                ProtectedApp("com.android.gallery", "Vault Gallery", "Gallery", true),
                ProtectedApp("com.android.mms", "Messages & Chat", "Messages", true),
                ProtectedApp("com.android.bank", "Apex Bank App", "Banking", true),
                ProtectedApp("com.android.social", "Social Sphere", "Social", false),
                ProtectedApp("com.android.custom", "Personal Ledger", "Custom", false)
            )
            dao.saveProtectedApps(defaultApps)
            return defaultApps
        }
        return apps
    }

    suspend fun saveProtectedApp(app: ProtectedApp) {
        dao.saveProtectedApp(app)
    }

    suspend fun insertUnlockLog(isSuccess: Boolean, method: String, details: String) {
        dao.insertUnlockLog(UnlockLog(isSuccess = isSuccess, method = method, details = details))
    }

    suspend fun clearUnlockLogs() {
        dao.clearUnlockLogs()
    }
}
