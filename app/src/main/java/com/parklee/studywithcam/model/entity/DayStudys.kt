package com.parklee.studywithcam.model.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class DayStudys (
    @Json(name="year") var year: String,  // 4자리
    @Json(name="month") var month: String,  // 1 - 12월
    @Json(name="day") var day: String,  // 1 - 31일
    @Json(name="time") var time: Int,  // 누적시간 - 초단위
    @field:Json(name="studys") var studys: List<Study>, // 공부구간
    @field:Json(name="focusXs") var focusXs: List<FocusX>  // 집중X구간
    )

data class StopStudys (
    @field:Json(name="studys") var studys: List<Study>,
    @field:Json(name="focusXs") var focusXs: List<FocusX>
    )

data class DayStudyCal (
    @Json(name="day") var day: String, // 일자
    @Json(name="level") var level: Int  // 3시간 단위의 레벨(5단계)
    )