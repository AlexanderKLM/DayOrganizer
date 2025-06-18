package com.example.dayorganizer

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class removeOldCardsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repository = (applicationContext as CardApplication).repository

        return withContext(Dispatchers.IO) {
            try {
                val fiveMonthsAgo = LocalDate.now().minusMonths(5)
                val fiveMonthsAgoString = fiveMonthsAgo.format(CardInfo.dateFormatter)
                val deletedCount = repository.deleteOldCards(fiveMonthsAgoString)
                println("WorkManager: Удалено карточек старше 5 месяцев: $deletedCount")
                Result.success()
            } catch (e: Exception) {
                println("WorkManager: Ошибка при удалении старых карточек: ${e.message}")
                Result.retry()
            }
        }
    }
}