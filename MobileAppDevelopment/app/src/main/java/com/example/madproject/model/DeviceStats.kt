package com.example.madproject.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * snapshot of device hardware/network state.
 * Used both for displaying in the UI and for inserting into Supabase.
 */
@Serializable
data class DeviceStats(
    @SerialName("battery_level") val batteryLevel: Int,
    @SerialName("is_charging") val isCharging: Boolean,
    @SerialName("device_model") val deviceModel: String,
    @SerialName("os_version") val osVersion: String,
    @SerialName("network_type") val networkType: String,
    @SerialName("wifi_name") val wifiName: String,
    @SerialName("ram_total_mb") val ramTotalMb: Long,
    @SerialName("ram_available_mb") val ramAvailableMb: Long,
    @SerialName("ram_used_mb") val ramUsedMb: Long,
    @SerialName("storage_total_gb") val storageTotalGb: Float,
    @SerialName("storage_free_gb") val storageFreeGb: Float,
    @SerialName("storage_used_gb") val storageUsedGb: Float
)
