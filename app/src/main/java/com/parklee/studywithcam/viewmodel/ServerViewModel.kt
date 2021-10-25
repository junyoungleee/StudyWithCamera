package com.parklee.studywithcam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parklee.studywithcam.network.ServerApi
import kotlinx.coroutines.launch

class ServerViewModel : ViewModel() {

    fun getDummy() {
        viewModelScope.launch {
            val listResult = ServerApi.retrofitService.getDummy()
            Log.d("결과", listResult)
        }
    }
}