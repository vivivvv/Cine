package com.app.cinema.roomdp

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface HistoryDao {

    @Query("SELECT * FROM HistoryData")
    fun getAll(): Flow<MutableList<HistoryData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(foodItem: HistoryData)

}
