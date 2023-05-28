package com.example.prayerapp.domain.usecase

import com.example.prayerapp.domain.repo.TimingsRepo
import javax.inject.Inject

class GetPrayersTimingsFromRemote @Inject constructor(private val timingsRepo: TimingsRepo) {

    suspend operator fun invoke(year: Int, month: Int, latitude: Double, longitude: Double) =
        timingsRepo.getPrayerFromRemote(year, month, latitude, longitude)


}