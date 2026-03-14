package com.example.madproject.ui.devicestats

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.madproject.R
import com.example.madproject.ui.home.HomeActivity
import kotlinx.coroutines.launch

class DeviceStatsActivity : AppCompatActivity() {

    private val viewModel: DeviceStatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        val batteryText = findViewById<TextView>(R.id.batteryText)
        val deviceText = findViewById<TextView>(R.id.deviceText)
        val networkText = findViewById<TextView>(R.id.networkText)
        val ramText = findViewById<TextView>(R.id.ramText)
        val storageText = findViewById<TextView>(R.id.storageText)
        val statusText = findViewById<TextView>(R.id.statusText)

        // Static info, this does not get observed
        deviceText.text = "Device: ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"

        findViewById<Button>(R.id.homeButton).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        // Needs location permission to read the WiFi SSID (Service Set Identifier)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)

        //Update the UI whenever new stats come in
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.stats?.let { stats ->
                        batteryText.text = "Battery: ${stats.batteryLevel}% ${if (stats.isCharging) "(Charging)" else ""}"
                        networkText.text = "Network: ${stats.networkType} ${if (stats.wifiName != "N/A") "(${stats.wifiName})" else ""}"
                        ramText.text = "RAM: ${stats.ramUsedMb}MB used / ${stats.ramTotalMb}MB total"
                        storageText.text = "Storage: ${"%.1f".format(stats.storageUsedGb)}GB used / ${"%.1f".format(stats.storageTotalGb)}GB total"
                    }
                    state.error?.let { statusText.text = it }
                    if (state.lastSaved == "Just now") statusText.text = "Last saved: just now"
                }
            }
        }
    }

    //Start the data collection once location permission is granted (or not)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            viewModel.startCollecting()
        }
    }
}
