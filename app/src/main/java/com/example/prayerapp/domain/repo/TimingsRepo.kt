package com.example.prayerapp.domain.repo

import com.example.prayerapp.domain.models.Data
import com.example.prayerapp.domain.models.PrayersApiResponse
import com.example.prayerapp.domain.models.Timings
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface TimingsRepo {

    suspend fun getPrayerFromRemote(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double
    ): Response<PrayersApiResponse>


    suspend fun insertTimings(timingsData:Data)

     fun getTimingsFromDatabase(): Flow<List<Data>>

}