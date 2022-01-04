package com.parklee.studywithcam.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.parklee.studywithcam.model.entity.DayStudyCal
import com.parklee.studywithcam.model.entity.DayStudys
import com.parklee.studywithcam.model.entity.StopStudys
import kotlinx.coroutines.launch
import retrofit2.http.*

class Repository {

    // retrofit -------------------------------------------------------------------------
    suspend fun postStudyData(uid: String, studyData: StopStudys) {
        ServerApi.retrofitService.postStudyData(uid, studyData)
    }

    suspend fun getDailyGraphData(uid: String, date: String) : List<DayStudys>{
        return ServerApi.retrofitService.getDailyGraphData(uid, date)
    }

    suspend fun getCalendarData(uid: String, month: Int) : List<DayStudyCal> {
        return ServerApi.retrofitService.getCalendarData(uid, month)
    }
}