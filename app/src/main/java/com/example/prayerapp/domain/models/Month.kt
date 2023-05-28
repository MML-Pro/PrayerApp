package com.example.prayerapp.domain.models


import com.google.gson.annotations.SerializedName

data class Month(
    @SerializedName("en")
    val en: String,
    @SerializedName("number")
    val number: Int
) {
    constructor():this("",0)
}