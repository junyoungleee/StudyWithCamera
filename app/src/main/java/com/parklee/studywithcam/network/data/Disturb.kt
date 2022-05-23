package com.parklee.studywithcam.network.data

import com.squareup.moshi.Json

data class Disturb (
    val date: String,
    @Json(name="type") val type: String,
    @Json(name="startTime") val startTime: String,
    @Json(name="endTime") val endTime: String)