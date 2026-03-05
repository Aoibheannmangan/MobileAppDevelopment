package com.example.madproject

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities  // ← was missing
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import android.Manifest
import androidx.core.app.ActivityCompat

@Serializable
data class DeviceStats (
    @SerialName("battery_level") val batteryLevel: Int,
    @SerialName("is_charging") val isCharging: Boolean,
    @SerialName("device_model") val deviceModel: String,
    @SerialName("os_version") val osVersion: String,
    @SerialName("network_type") val networkType: String,
    @SerialName("wifi_name") val wifiName: String
)

class DeviceStatsActivity : AppCompatActivity() {
    private lateinit var batteryText: TextView
    private lateinit var deviceText: TextView
    private lateinit var networkText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        //REquest location permission for WiFi SSID
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001
        )

        batteryText = findViewById(R.id.batteryText)
        deviceText = findViewById(R.id.deviceText)
        networkText = findViewById(R.id.networkText)
        statusText = findViewById(R.id.statusText)

        //Show static device info immediately since it never changes
        deviceText.text = "Device: ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"

    }

    /**
     * Launches a coroutine that collects device stats
     * Saves to Supabase every 30 secs as long as the activity is alive
     */
    private fun startCollecting() {
        lifecycleScope.launch {
            while(true){
                try{
                    val stats = collectStats()
                    saveToSupabase(stats)
                    updateUI(stats)
                    statusText.text = "Last saved: just now"
                } catch (e: Exception){
                    android.util.Log.e("DeviceStatsError", "Failed", e)
                    statusText.text = "Save failed: ${e.message}"
                }

                //Wait 30 secs before collecting again
                delay(30_000)
            }
        }
    }

    /**
     * Reads current info from system
     * Returns deviceStats object ready to be saved to Supabase
     * We pumping adrenaline
     */
    private fun collectStats(): DeviceStats {
        //Battery
        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (scale > 0) (level * 100 /scale) else -1
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        //Network
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val networkType = when {
            capabilities == null -> "No connection"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile Data"
            else -> "Other"
        }

        //Wifi
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiName = if (networkType == "WiFi") {
           val hasPermission = ContextCompat.checkSelfPermission(
               this, Manifest.permission.ACCESS_FINE_LOCATION
           ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                wifiManager.connectionInfo.ssid.replace("\"", "")
            } else {
                "Permission Needed"
            }
        } else {
            "N/A"
        }

        return DeviceStats(
            batteryLevel = batteryPct,
            isCharging = isCharging,
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            osVersion = "Android ${Build.VERSION.RELEASE}",
            networkType = networkType,
            wifiName = wifiName
        )
    }

    //Saves the collected stats to the device_stats table in Supabase
    private suspend fun saveToSupabase(stats: DeviceStats) {
        SupabaseClientProvider.client.from("device_stats").insert(stats)
    }

    //Update TextViews
    private fun updateUI(stats: DeviceStats) {
        batteryText.text = "Battery: ${stats.batteryLevel}% ${if (stats.isCharging) "(Charging)" else ""}"
        networkText.text = "Network: ${stats.networkType} ${if (stats.wifiName != "N/A") "(${stats.wifiName})" else ""}"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            startCollecting()
        }
    }
}