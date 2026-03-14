package com.example.madproject.ui.devicestats

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.madproject.data.repository.DeviceStatsRepository

class DeviceStatsWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val repository = DeviceStatsRepository(applicationContext)
            repository.collectAndSave()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
