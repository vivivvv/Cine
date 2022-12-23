package com.app.cinema.movieDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.cinema.history.HistoryRepository
import com.app.cinema.roomdp.HistoryData
import kotlinx.coroutines.launch

class MovieDetailsViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    fun insert(historyData: HistoryData) = viewModelScope.launch {
        historyRepository.insert(historyData)
    }

    class MovieDetailsViewModelFactory(private val repository: HistoryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
                return MovieDetailsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}