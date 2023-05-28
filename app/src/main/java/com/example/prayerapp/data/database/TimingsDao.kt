package com.example.prayerapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.prayerapp.domain.models.Data
import kotlinx.coroutines.flow.Flow

@Dao
interface TimingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimingsData(timingsData:Data)

    @Query("SELECT * FROM timings_data_table")
    fun getTimingsData() : Flow<List<Data>>
}