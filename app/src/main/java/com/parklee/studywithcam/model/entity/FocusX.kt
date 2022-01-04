package com.parklee.studywithcam.model.entity

import com.squareup.moshi.Json

data class FocusX (
    @Json(name="type") var type: String, // '졻'
    @Json(name="startTime") var startTime: String, // 'hhmmss'
    @Json(name="endTime") var endTime: String  // hhmmss
    )