package com.parklee.studywithcam.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET

private const val BASE_URL = "https://172.20.10.2:2443/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

object ServerApi {
    val retrofitService : ServerApiService by lazy {
        retrofit.create(ServerApiService::class.java)
    }
}

interface ServerApiService {

    @GET("statics")
    suspend fun getDummy(): String
}