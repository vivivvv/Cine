package com.app.cinema

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.cinema.model.Search

class MainViewModel : ViewModel() {

    var movieList = ArrayList<Search>()
    var historyList = ArrayList<String>()


}