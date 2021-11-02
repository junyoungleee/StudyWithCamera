package com.parklee.studywithcam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry

class GraphViewModel : ViewModel() {

    // Entry 타입 사용 (x: Float, y: Float)
    // 레벨 : 0 ~ 5 (20분마다 레벨 1 상승, 집중력 저하 시 레벨 1 하강)

    private val _graphData = MutableLiveData<Entry>()
    val graphData: LiveData<Entry>
        get() = _graphData

    fun levelDown(time: Float) {
        // 집중력 저하 시, 레벨 1

    }

    fun levelUp() {
        // 20분마다 레벨 1 상승

    }

    fun stopStudy() {
        // 공부를 끝내면 레벨 0
    }
}