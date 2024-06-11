package com.dicoding.asclepius.view.result

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ResultViewModelFactory private constructor(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ResultViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application): ResultViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ResultViewModelFactory::class.java) {
                    INSTANCE = ResultViewModelFactory(application)
                }
            }
            return INSTANCE as ResultViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(mApplication) as T
        } else if (modelClass.isAssignableFrom(ResultAddViewModel::class.java)) {
            return ResultAddViewModel(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}