package com.parklee.studywithcam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class TimerViewModel : ViewModel() {

    private lateinit var timeTask: Job

    private var nSec: Int = 0
    private var cSec: Int = 0

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

//    fun stopTimer1(initSec: Int = sec) {
//        timeTask?.cancel()
//        sec = initSec
//        _time.value = sec
//    }
}