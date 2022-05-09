package com.parklee.studywithcam.viewmodel

import androidx.lifecycle.*
import com.parklee.studywithcam.network.data.Disturb
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class TimerViewModel() : ViewModel() {

    private lateinit var timeTask: Job

    private var nSec: Int = 0  // 지금 공부하는 시간량
    private var cSec: Int = 0  // 누적 시간량

    private val _nTime = MutableLiveData<Int>()
    private val _cTime = MutableLiveData<Int>()
    val nTime: LiveData<Int>
        get() = _nTime
    val cTime: LiveData<Int>
        get() = _cTime

    init {
        _nTime.value = nSec
    }

    fun startTimer(init: Int) {
        viewModelScope.launch {
            cSec = init
            timeTask = viewModelScope.launch {
                while (true) {
                    nSec++
                    cSec++
                    _nTime.value = nSec
                    _cTime.value = cSec
                    delay(1000L)
                }
            }
        }
    }

    fun stopTimer() {
        timeTask?.cancel()
    }



}


