package com.app.cinema

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.cinema.history.HistoryRepository
import com.app.cinema.model.Search

class MainViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    var movieList = ArrayList<Search>()
    var historyList = ArrayList<String>()

    class MainViewModelFactory(private val repository: HistoryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}