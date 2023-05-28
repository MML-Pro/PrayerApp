package com.example.prayerapp.domain.models


import com.google.gson.annotations.SerializedName

class Meta(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("latitudeAdjustmentMethod")
    val latitudeAdjustmentMethod: String,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("method")
    val method: Method,
    @SerializedName("midnightMode")
    val midnightMode: String,
    @SerializedName("offset")
    val offset: Offset,
    @SerializedName("school")
    val school: String,
    @SerializedName("timezone")
    val timezone: String
){
    constructor() : this(0.0, "", 0.0, Method(), "", Offset(), "", "")

}