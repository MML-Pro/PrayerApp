package com.example.prayerapp.domain.models


import com.google.gson.annotations.SerializedName

data class Weekday(
    @SerializedName("en")
    val en: String
) {
    constructor() :this("")
}