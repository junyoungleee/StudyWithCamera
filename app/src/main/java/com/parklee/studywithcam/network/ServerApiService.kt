package com.parklee.studywithcam.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.*
import java.util.*
import kotlin.collections.HashMap

private const val BASE_URL = ""
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

    @POST("stopTimer")
    suspend fun postDummy(@Body params: HashMap<String, Any>)

}