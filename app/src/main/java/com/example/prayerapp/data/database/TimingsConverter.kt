package com.example.prayerapp.data.database

import androidx.room.TypeConverter
import com.example.prayerapp.domain.models.Timings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimingsConverter {
    @TypeConverter
    fun fromTimings(timings: Timings): String {
        return Gson().toJson(timings)
    }

    @TypeConverter
    fun toTimings(timingsString: String): Timings {
        val type = object : TypeToken<Timings>() {}.type
        return Gson().fromJson(timingsString, type)
    }
}
