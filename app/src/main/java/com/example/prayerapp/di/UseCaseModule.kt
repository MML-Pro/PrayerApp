package com.example.prayerapp.di

import com.example.prayerapp.domain.repo.TimingsRepo
import com.example.prayerapp.domain.usecase.GetPrayersTimingsFromRemote
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    fun provideUseCase(timingsRepo: TimingsRepo):GetPrayersTimingsFromRemote{
        return GetPrayersTimingsFromRemote(timingsRepo)
    }


}