package com.parklee.studywithcam.network


import com.parklee.studywithcam.model.entity.DayStudys
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type
import java.util.*

interface ServerApiService {

//    // 타이머 멈췄을 때, 서버에 공부구간 데이터 보냄
//    @Headers("content-type: application/json")
//    @POST("stopTimer/{id}")
//    suspend fun postStudyData(@Path("id") uid: String,
//                              @Body studyData: StopStudys)
//
//    // 통계(일간) - 끈기그래프
//    @GET("statics/{id}/{date}")
//    suspend fun getDailyGraphData(@Path("id") uid: String,
//                                  @Path("date") date: String): List<DayStudys>
//
//    // 통계 - 캘린더 컬러 데이터
//    @GET("calendar/{id}/{month}")
//    suspend fun getCalendarData(@Path("id") uid: String,
//                                @Path("month") month: Int): List<DayStudyCal>

}


