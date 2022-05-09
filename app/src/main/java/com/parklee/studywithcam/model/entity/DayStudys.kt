package com.parklee.studywithcam.model.entity

import androidx.room.Entity
import com.squareup.moshi.Json

@Entity
data class DayStudys (
    @Json(name="year") var year: String,  // 4자리
    @Json(name="month") var month: String,  // 1 - 12월
    @Json(name="day") var day: String,  // 1 - 31일
    @Json(name="time") var time: Int,  // 누적시간 - 초단위
    @field:Json(name="studys") var studys: List<Study>, // 공부구간
    @field:Json(name="focusXs") var focusXs: List<Disturb>  // 집중X구간
)
