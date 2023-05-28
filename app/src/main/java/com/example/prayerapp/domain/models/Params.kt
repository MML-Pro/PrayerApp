package com.example.prayerapp.domain.models


import com.google.gson.annotations.SerializedName

data class Params(
    @SerializedName("Fajr")
    val fajr: Int,
    @SerializedName("Isha")
    val isha: Int
){
    constructor() :this(0,0)
}