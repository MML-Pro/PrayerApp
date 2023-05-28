package com.example.prayerapp.domain.models


import com.google.gson.annotations.SerializedName

data class PrayersApiResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
)