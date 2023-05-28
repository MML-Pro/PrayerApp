package com.example.prayerapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.prayerapp.domain.models.Data
import com.example.prayerapp.domain.models.Timings

@Database(
    entities = [Data::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimingsConverter::class)
abstract class TimingsDatabase : RoomDatabase() {

    abstract val timingsDao: TimingsDao

    companion object {

        @Volatile
        private var INSTANCE: TimingsDatabase? = null

        fun getInstance(context: Context) :TimingsDatabase{

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TimingsDatabase::class.java,
                        "timings_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }


        }

    }

}