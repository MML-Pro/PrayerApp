package com.example.prayerapp.di

import com.example.prayerapp.data.database.TimingsDao
import com.example.prayerapp.data.remote.PrayersAPIService
import com.example.prayerapp.data.repo.TimingsRepoImpl
import com.example.prayerapp.domain.repo.TimingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    fun provideRepo(apiService: PrayersAPIService, timingsDao: TimingsDao): TimingsRepo{
        return TimingsRepoImpl(apiService,timingsDao)
    }
}