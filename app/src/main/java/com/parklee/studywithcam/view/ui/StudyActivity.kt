package com.parklee.studywithcam.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.databinding.ActivityStudyBinding
import com.parklee.studywithcam.viewmodel.TimerViewModel

class StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding
    private lateinit var timerVM: TimerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var init = SWCapplication.pref.getPrefTime("cTime")

        timerVM = ViewModelProvider(this).get(TimerViewModel::class.java)
        timerVM.startTimer(init)  // 현재 타이머: 0, 누적 타이머: init

        timerVM.nTime.observe(this, Observer { time ->
            binding.studyNowTv.text = calSecToString(time)
        })

        timerVM.cTime.observe(this, Observer { time ->
            binding.studyCumulTv.text = calSecToString(time)
        })

        // 돌아가기
        stopTimerButtonAction()
    }

    //----------------------------------------------------------------------------------
    private fun calSecToString(time: Int): String {
        var hour = time / 3600
        var min = (time - (hour*3600)) / 60
        var sec = (time - (hour*3600)) % 60
        return "${makeText(hour)} : ${makeText(min)} : ${makeText(sec)}"
    }

    private fun makeText(t: Int): String {
        if (t < 10) return "0${t}" else return "$t"
    }

    //----------------------------------------------------------------------------------
    // 타이머 멈춤 & 저장
    private fun stopTimerButtonAction() {
        binding.studyTimerButton.setOnClickListener {
            timerVM.stopTimer()

            SWCapplication.pref.setPrefTime("cTime", timerVM.cTime.value!!.toInt())
            SWCapplication.pref.setPrefTime("nTime", timerVM.nTime.value!!.toInt())

            finish()
        }
    }

}


