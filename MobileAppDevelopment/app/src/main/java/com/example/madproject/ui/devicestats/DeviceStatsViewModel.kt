package com.example.madproject.ui.devicestats

//Used GenerativeAi for lines 42 to 58 to integrate the worker class with what was currently there
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.repository.DeviceStatsRepository
import com.example.madproject.model.DeviceStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

data class DeviceStatsUiState(
    val stats: DeviceStats? = null,
    val lastSaved: String = "Not saved yet",
    val error: String? = null
)

//The collection loop is here instead of DeviceStatsActivity
//This is so it survives screen rotation!
class DeviceStatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DeviceStatsRepository(application)
    private val workManager = WorkManager.getInstance(application)

    private val _uiState = MutableStateFlow(DeviceStatsUiState())
    val uiState: StateFlow<DeviceStatsUiState> = _uiState.asStateFlow()

    companion object {
        const val WORK_TAG = "device_stats_sync"
    }

    fun startCollecting() {
        val workRequest = PeriodicWorkRequestBuilder<DeviceStatsWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(WORK_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP, //THis means it doesnt restart if its already scheduled
            workRequest
        )
        //immediate one-shot collection for the UI
        collectForUi()
    }

    fun collectForUi(){
        viewModelScope.launch {
            while (true) {
                try {
                    // collectAndSave() handles Dispatchers.IO internally
                    val stats = repository.collectAndSave()
                    _uiState.update { it.copy(stats = stats, lastSaved = "Just now", error = null) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Save failed: ${e.message}") }
                }
                delay(30_000)
            }
        }
    }

    fun stopCollecting() {
        workManager.cancelAllWorkByTag(WORK_TAG)
    }
}
