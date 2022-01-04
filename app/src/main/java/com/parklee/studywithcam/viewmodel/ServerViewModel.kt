package com.parklee.studywithcam.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.parklee.studywithcam.model.entity.DayStudyCal
import com.parklee.studywithcam.model.entity.DayStudys
import com.parklee.studywithcam.model.entity.StopStudys
import com.parklee.studywithcam.network.Repository
import com.parklee.studywithcam.network.ServerApi
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap

class ServerViewModel(private val repository: Repository) : ViewModel() {

    fun postStudyData(uid: String, studyData: StopStudys) {
        viewModelScope.launch {
            repository.postStudyData(uid, studyData)
        }
    }

    val graph_data: MutableLiveData<List<DayStudys>> = MutableLiveData()
    fun getDailyGraphData(uid: String, date: String) {
        viewModelScope.launch {
            var graphList = repository.getDailyGraphData(uid, date)
            graph_data.value = graphList
        }
    }

    val cal_data: MutableLiveData<List<DayStudyCal>> = MutableLiveData()
    fun getCalendarData(uid: String, month: Int) {
        viewModelScope.launch {
            var calList = repository.getCalendarData(uid, month)
            cal_data.value = calList
        }
    }
}

class ServerViewModelFactory(private val repository: Repository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ServerViewModel(repository) as T
    }
    }

