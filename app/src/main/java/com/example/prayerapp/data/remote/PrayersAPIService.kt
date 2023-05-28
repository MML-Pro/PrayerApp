package com.example.prayerapp.data.remote

import com.example.prayerapp.domain.models.PrayersApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayersAPIService {

    @GET("calendar/{year}/{month}")
    suspend fun getPrayer(
        @Path("year") year:Int,
        @Path("month") month:Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 2
    ): Response<PrayersApiResponse>
}