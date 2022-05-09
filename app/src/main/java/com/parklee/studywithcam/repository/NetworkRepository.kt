package com.parklee.studywithcam.repository

import com.parklee.studywithcam.model.entity.DayStudys
import com.parklee.studywithcam.network.ApiBuilder
import com.parklee.studywithcam.network.ServerApiService

class NetworkRepository {

    private val studyApi: ServerApiService by lazy {
        ApiBuilder.retrofit.create(ServerApiService::class.java)
    }
}