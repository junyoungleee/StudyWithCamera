package com.parklee.studywithcam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parklee.studywithcam.network.ServerApi
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class ServerViewModel : ViewModel() {

    fun getDummy() {
        viewModelScope.launch {
            val listResult = ServerApi.retrofitService.getDummy()
            Log.d("GET 결과", listResult)
        }
    }

    fun postDummy(param: HashMap<String, Any>) {
        viewModelScope.launch {
            ServerApi.retrofitService.postDummy(param)
        }
    }
}