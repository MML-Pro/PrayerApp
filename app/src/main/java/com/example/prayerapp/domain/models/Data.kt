package com.example.prayerapp.domain.models


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "timings_data_table")
data class Data(

    @PrimaryKey(autoGenerate = true)
    var id:Int,
    @SerializedName("date")
    @Ignore
    val date: Date,
    @SerializedName("meta")
    @Ignore
    val meta: Meta,
    @SerializedName("timings")
    var timings: Timings
){
    constructor() : this(0,Date(), Meta(), Timings())

}