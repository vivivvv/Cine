package com.app.cinema.roomdp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryData(
    @PrimaryKey @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image") val image: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "imdbID") val imdbID: String?
)