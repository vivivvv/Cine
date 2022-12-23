package com.app.cinema.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.app.cinema.roomdp.HistoryData

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    val allHistoryData: LiveData<MutableList<HistoryData>> =
        historyRepository.allHistoryData.asLiveData()

    class HistoryViewModelFactory(private val repository: HistoryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                return HistoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}