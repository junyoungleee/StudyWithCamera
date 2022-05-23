package com.parklee.studywithcam.network.data

import com.squareup.moshi.Json

data class Study(
    val date: String,
    @Json(name="startTime") val startTime: String,
    @Json(name="endTime") val endTime: String,
    @Json(name="time") val time: Int
)