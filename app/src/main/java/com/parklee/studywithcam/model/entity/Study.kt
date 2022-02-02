package com.parklee.studywithcam.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class Study(
    @PrimaryKey var date: String,
    @Json(name="startTime") var startTime: String,
    @Json(name="endTime") var endTime: String,
    @Json(name="time") var time: Int
)