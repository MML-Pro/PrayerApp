package com.example.prayerapp.domain.usecase

import com.example.prayerapp.domain.models.Data
import com.example.prayerapp.domain.repo.TimingsRepo
import javax.inject.Inject

class InsertPrayersTimings @Inject constructor(private val timingsRepo: TimingsRepo) {

   suspend operator fun invoke(timingsData: Data) = timingsRepo.insertTimings(timingsData)
}