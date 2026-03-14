package com.example.madproject.ui.devicestats

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Permission handling in Compose:
 * rememberLauncherForActivityResult replaces onRequestPermissionsResult.
 * The lambda runs when the user responds to the permission dialog.
 * LaunchedEffect(Unit) fires once on first composition (like onCreate), launching the request.
 */
@Composable
fun DeviceStatsScreen(
    onHomeClick: () -> Unit,
    viewModel: DeviceStatsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* granted or denied — start collecting either way, repo handles missing permission */ ->
        viewModel.startCollecting()
    }

    // Request location permission once when screen appears, then start collecting
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Device Stats", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Device: ${Build.MODEL} (Android ${Build.VERSION.RELEASE})")
        Spacer(modifier = Modifier.height(16.dp))

        state.stats?.let { stats ->
            Text("Battery: ${stats.batteryLevel}% ${if (stats.isCharging) "(Charging)" else ""}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Network: ${stats.networkType} ${if (stats.wifiName != "N/A") "(${stats.wifiName})" else ""}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("RAM: ${stats.ramUsedMb}MB used / ${stats.ramTotalMb}MB total")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Storage: ${"%.1f".format(stats.storageUsedGb)}GB used / ${"%.1f".format(stats.storageTotalGb)}GB total")
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (state.lastSaved == "Just now") {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Last saved: just now")
        }

        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onHomeClick, modifier = Modifier.fillMaxWidth()) {
            Text("Home")
        }
    }
}
