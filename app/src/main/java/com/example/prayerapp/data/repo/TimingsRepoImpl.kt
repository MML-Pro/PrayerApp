package com.example.prayerapp.data.repo

import com.example.prayerapp.data.database.TimingsDao
import com.example.prayerapp.data.remote.PrayersAPIService
import com.example.prayerapp.domain.models.Data
import com.example.prayerapp.domain.models.PrayersApiResponse
import com.example.prayerapp.domain.repo.TimingsRepo
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class TimingsRepoImpl @Inject constructor(
    private val prayersAPIService: PrayersAPIService,
    private val timingsDao: TimingsDao

) : TimingsRepo {
    override suspend fun getPrayerFromRemote(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double
    ): Response<PrayersApiResponse> {
        return prayersAPIService.getPrayer(year, month, latitude, longitude)
    }

    override suspend fun insertTimings(timingsData: Data) {
        timingsDao.insertTimingsData(timingsData)
    }

    override fun getTimingsFromDatabase(): Flow<List<Data>> {
        return timingsDao.getTimingsData()
    }

}