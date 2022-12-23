package com.app.cinema.history

import androidx.annotation.WorkerThread
import com.app.cinema.roomdp.HistoryDao
import com.app.cinema.roomdp.HistoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HistoryRepository(private val historyDao: HistoryDao) {

    val allHistoryData: Flow<MutableList<HistoryData>> = historyDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(historyData: HistoryData) {
        withContext(Dispatchers.IO) {
            historyDao.insert(historyData)
        }
    }


}