package com.dicoding.asclepius.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.database.HistoryDao
import com.dicoding.asclepius.database.HistoryRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {
    private val mHistoriesDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = HistoryRoomDatabase.getDatabase(application)
        mHistoriesDao = db.historyDao()
    }
    fun getAllHistories(): LiveData<List<History>> = mHistoriesDao.getAllHistories()

    fun insert(history: History) {
        executorService.execute { mHistoriesDao.insert(history) }
    }
    fun delete(history: History) {
        executorService.execute { mHistoriesDao.delete(history) }
    }
}