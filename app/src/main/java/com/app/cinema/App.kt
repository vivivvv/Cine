package com.app.cinema

import android.app.Application
import com.app.cinema.history.HistoryRepository
import com.app.cinema.roomdp.MyAppDatabase

class App : Application() {

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { MyAppDatabase.getDatabase(this) }
    val repository by lazy { HistoryRepository(database.historyDao()) }
}