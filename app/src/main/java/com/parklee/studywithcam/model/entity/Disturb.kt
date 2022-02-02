package com.parklee.studywithcam.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class Disturb (
    @PrimaryKey var date: String,
    @Json(name="type") var type: String, // '졻'
    @Json(name="startTime") var startTime: String, // 'hhmmss'
    @Json(name="endTime") var endTime: String,  // hhmmss
    @Json(name="time") var time: Int
    )