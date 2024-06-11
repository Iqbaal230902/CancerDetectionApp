package com.dicoding.asclepius.view.result

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.repository.HistoryRepository

class ResultViewModel(application: Application) : ViewModel() {
    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)
    fun getAllHistories(): LiveData<List<History>> = mHistoryRepository.getAllHistories()
}