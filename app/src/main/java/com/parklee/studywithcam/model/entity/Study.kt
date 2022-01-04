package com.parklee.studywithcam.model.entity

import com.squareup.moshi.Json

data class Study(
    @Json(name="startTime") var startTime: String,
    @Json(name="endTime") var endTime: String
)