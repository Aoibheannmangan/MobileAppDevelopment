class DeviceStatsWorker (
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