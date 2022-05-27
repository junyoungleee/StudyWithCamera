package com.parklee.studywithcam.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study
import com.parklee.studywithcam.repository.DatabaseRepository
import com.parklee.studywithcam.repository.NetworkRepository
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.vision.BLINK_STATE
import com.parklee.studywithcam.vision.HEAD_STATE
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StudyViewModel(private val dbRepo: DatabaseRepository): ViewModel()  {

    var disturbs: MutableList<Disturb> = mutableListOf()
    // 얼굴이 없으면 공부하지 않는 것
    private val _notStudying: MutableLiveData<Boolean> = MutableLiveData(false)
    val notStudying: LiveData<Boolean> get() = _notStudying
    private var emptyFace = 0

    private var beforeGazeResult: String = ""    // left, center, right

    var gazeCount: Int = 0
    var blinkCount: Int = 0
    var wakeUpCount: Int = 0
    var headUpCount: Int = 0

    val SPACE_OUT = 10
    val DROWSINESS = 10
    val WAKE_UP = 10

    var isSpacedOut: Boolean = false
    var isDrowsed: Boolean = false

    var disturbStartTime: String = ""

    fun analyzeDisturb(blink: BLINK_STATE, gaze: String, head: HEAD_STATE) {
        if (blink == BLINK_STATE.CLOSE) {
            blinkCount += 1
            if (blinkCount == DROWSINESS) {
                // 졸음 상태 시작
                isDrowsed = true
                initDisturbSectionStart()
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
                    makeDisturbSection("졸음")
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
                analogySpaceOut(gaze, head)
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

    private fun makeDisturbSection(type: String) {
        val today = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()).toString()
        val end = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())
        val time = (SimpleDateFormat("HH:mm:ss").parse(today).time - SimpleDateFormat("HH:mm:ss").parse(end).time) / 1000
        val disturb = Disturb(today, type, disturbStartTime, end, time.toInt())
        disturbs.add(disturb)
    }

    private fun initDisturbSectionStart() {
        disturbStartTime = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())
    }

    private fun analogySpaceOut(gaze: String, head: HEAD_STATE) {
        if (head == HEAD_STATE.UP) {
            headUpCount += 1
            if (headUpCount == SPACE_OUT) {
                // 고개를 든 멍때림 상태 시작
                initDisturbSectionStart()
                isSpacedOut = true
            }
        } else {
            if (isSpacedOut) {
               if (headUpCount >= SPACE_OUT) {
                   // 고개를 든 멍때림 상태 종료
                   isSpacedOut = false
                   headUpCount = 0
                   makeDisturbSection("멍때림")
               } else if (gazeCount >= SPACE_OUT) {
                   // 한 곳 응시 멍때림 상태
                   if (gaze != beforeGazeResult) {
                       initGazeState(gaze)
                       makeDisturbSection("멍때림")
                   } else{
                       gazeCount += 1
                   }
               }
            } else {
                // 고개를 숙이고 여러 곳을 응시하는 공부 상태
                if (gaze == beforeGazeResult) {
                    gazeCount += 1
                    if (gazeCount == SPACE_OUT) {
                        // 한 곳 응시 멍때림 상태 시작
                        isSpacedOut = true
                        initDisturbSectionStart()
                    }
                } else {
                    initGazeState(gaze)
                }
            }
        }
    }

    private fun initGazeState(gaze: String) {
        beforeGazeResult = gaze
        gazeCount = 0
    }

    fun saveDisturbSection(){
        if (disturbs.isNotEmpty()) {
            viewModelScope.launch {
                disturbs.map { disturb ->
                    dbRepo.insertDisturbSection(disturb)
                }
            }
        }
    }

    var studyStartTime: String = ""

    fun setStudyStartTime() {
        studyStartTime = LocalDate.now().toString()
    }

    fun saveStudySection() {
        val today = LocalDate.now().toString()
        val end = ClockFormat.convertLocalDateToTime()
        val gap = ClockFormat.calculateTimeGap(studyStartTime, end)
        viewModelScope.launch {
            dbRepo.insertStudySection(Study(today, studyStartTime, end, gap))
        }
    }


}

class StudyViewModelFactory(private val dbRepo: DatabaseRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyViewModel(dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}