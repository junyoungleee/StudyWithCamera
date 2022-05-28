package com.parklee.studywithcam.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyStudy (
    @PrimaryKey val date: String,
    val time: Int )