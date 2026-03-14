package com.example.madproject.data.repository

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.core.content.ContextCompat
import com.example.madproject.data.remote.SupabaseClientProvider
import com.example.madproject.model.DeviceStats
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//This is where all the hardware collection logic is, so its testable in isolation
//Takes Application context to avoid leaking an Activity reference
class DeviceStatsRepository(private val context: Context) {

    //Collects the device stats and saves them to supabase.
    //Result is returned for UI
    suspend fun collectAndSave(): DeviceStats = withContext(Dispatchers.IO) {
        val stats = collectStats()
        SupabaseClientProvider.client.from("device_stats").insert(stats)
        stats
    }

    fun collectStats(): DeviceStats {
        // Battery stats
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (scale > 0) (level * 100 / scale) else -1
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // Network stats
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val networkType = when {
            capabilities == null -> "No connection"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile Data"
            else -> "Other"
        }

        // WiFi SSID (requires location permission on Android 8+)
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiName = if (networkType == "WiFi") {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) wifiManager.connectionInfo.ssid.replace("\"", "") else "Permission Needed"
        } else "N/A"

        // RAM stats
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }
        val ramTotalMb = memInfo.totalMem / (1024 * 1024)
        val ramAvailableMb = memInfo.availMem / (1024 * 1024)

        // Storage stats
        val statFs = StatFs(Environment.getDataDirectory().path)
        val storageTotalGb = statFs.totalBytes / (1024f * 1024f * 1024f)
        val storageFreeGb = statFs.freeBytes / (1024f * 1024f * 1024f)

        return DeviceStats(
            batteryLevel = batteryPct,
            isCharging = isCharging,
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            osVersion = "Android ${Build.VERSION.RELEASE}",
            networkType = networkType,
            wifiName = wifiName,
            ramTotalMb = ramTotalMb,
            ramAvailableMb = ramAvailableMb,
            ramUsedMb = ramTotalMb - ramAvailableMb,
            storageTotalGb = storageTotalGb,
            storageFreeGb = storageFreeGb,
            storageUsedGb = storageTotalGb - storageFreeGb
        )
    }
}
