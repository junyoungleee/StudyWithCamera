package com.parklee.studywithcam.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parklee.studywithcam.network.data.Disturb
import java.text.SimpleDateFormat

class StudyViewModel: ViewModel()  {

    var disturbs: MutableList<Disturb> = mutableListOf()
    // 얼굴이 없으면 공부하지 않는 것
    private val _notStudying: MutableLiveData<Boolean> = MutableLiveData(false)
    val notStudying: LiveData<Boolean> get() = _notStudying
    private var emptyFace = 0

    private var gazeResult: String = ""    // left, center, right
    private var blinkResult: String = ""   // open, close

    var gazeCount: Int = 0
    var blinkCount: Int = 0
    var wakeUpCount: Int = 0

    val SPACE_OUT = 5
    val DROWSINESS = 5
    val WAKE_UP = 5

    var isSpacedOut: Boolean = false
    var isDrowsed: Boolean = false

    var disturbStartTime: String = ""

    fun detectDrowsiness(result: Int) {
        if (result == 1) {
            blinkCount += 1
            if (blinkCount == DROWSINESS) {
                // 졸음 상태 시작
                isDrowsed = true
                disturbStartTime = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()).toString()
            }
            if (wakeUpCount > 0 && isDrowsed) {
                // 졸음 상태 -> 중간에 눈 깜빡임을 감지
                wakeUpCount = 0
            }
        } else {
            if (isDrowsed) {
                // 졸음 상태 -> 눈 뜬 것이 감지된 경우
                wakeUpCount += 1
                if (wakeUpCount == WAKE_UP) {
                    val today = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()).toString()
                    val end = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()).toString()
                    val disturb = Disturb(today, "졸음", disturbStartTime, end)
                    disturbs.add(disturb)
                    isDrowsed = false
                    blinkCount = 0
                    wakeUpCount = 0
                    Log.d("disturb_drowsiness", "${disturbs}")
                }
            } else {
                if (blinkCount > 0) {
                    // 졸음 상태X -> 깜빡임을 잘못 감지한 경우
                    blinkCount = 0
                }
                // 졸음 상태 X -> 눈 뜬 것이 감지된 경우
                // detectGaze()
            }
        }
        Log.d("disturb_result", "blink : $blinkCount, wake : $wakeUpCount, isDrowsed: $isDrowsed")
    }

    fun detectEmptyFace() {
        // 얼굴이 감지되지 않을 때
        emptyFace += 1
        if (emptyFace == 5) {
            _notStudying.value = true
        }
    }

    fun detectGaze(result: String) {
        when {
            gazeResult == "" || gazeResult != result -> {
                gazeResult = result
            }
            gazeResult == result -> {
                if(gazeCount == SPACE_OUT) {

                } else {
                    gazeCount += 1
                }
            }
        }
    }

}