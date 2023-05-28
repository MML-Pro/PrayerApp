package com.example.prayerapp.domain.models


import com.google.gson.annotations.SerializedName

 class Method(
    @SerializedName("id")
    val id: Int,
    @SerializedName("location")
    val location: Location,
    @SerializedName("name")
    val name: String,
    @SerializedName("params")
    val params: Params
) {
    constructor() : this(0, Location(0.0, 0.0), "", Params(0,0))

 }