package com.example.prayerapp.domain.models


import com.google.gson.annotations.SerializedName

data class WeekdayX(
    @SerializedName("ar")
    val ar: String,
    @SerializedName("en")
    val en: String
){
    constructor():this("","")
}