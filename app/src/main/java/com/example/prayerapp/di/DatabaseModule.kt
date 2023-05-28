package com.example.prayerapp.di

import android.content.Context
import com.example.prayerapp.data.database.TimingsDao
import com.example.prayerapp.data.database.TimingsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) : TimingsDatabase{
        return TimingsDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideDao(timingsDatabase: TimingsDatabase) : TimingsDao{
        return timingsDatabase.timingsDao
    }

}