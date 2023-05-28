package com.example.prayerapp.domain.usecase

import com.example.prayerapp.domain.repo.TimingsRepo
import javax.inject.Inject

class GetPrayersTimingsFromDB @Inject constructor(private val timingsRepo: TimingsRepo) {

    operator fun invoke() = timingsRepo.getTimingsFromDatabase()
}