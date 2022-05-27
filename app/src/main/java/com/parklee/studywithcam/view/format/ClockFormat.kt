package com.parklee.studywithcam.view.format

import android.provider.Settings.Global.getString
import com.parklee.studywithcam.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class ClockFormat {
    // 타이머 String 생성
    fun calSecToString(time: Int): String {
        var hour = time / 3600
        var min = (time - (hour*3600)) / 60
        var sec = (time - (hour*3600)) % 60
        return "${makeText(hour)} : ${makeText(min)} : ${makeText(sec)}"
    }

    fun calSecToKorean(time: Int): String {
        var hour = time / 3600
        var min = (time - (hour*3600)) / 60
        return "${makeText(hour)}시간 ${makeText(min)}분"
    }

    private fun makeText(t: Int): String {
        if (t < 10) return "0${t}" else return "$t"
    }

    companion object {
        fun convertTimestampToTime(timestamp: Long): String{
            val sdf = SimpleDateFormat("hh:mm:ss")
            val time = sdf.format(timestamp)

            return time
        }

        fun convertLocalDateToTime(): String{
            val now = LocalDateTime.now()
            val sdf = SimpleDateFormat("hh:mm:ss")
            val time = sdf.format(now)

            return time
        }

        fun calculateTimeGap(start: String, end: String): Int {
            val s = start.split(":")
            val e = end.split(":")

            val startTime = s[0].toInt()*3600 + s[1].toInt()*60 + s[2].toInt()
            val endTime = e[0].toInt()*3600 + e[1].toInt()*60 + e[2].toInt()

            return endTime - startTime
         }
    }

    // 오늘 공부한 시간과 평균량의 차이 메세지 메서드
//    fun calTimeGap(today: Int, avgSec: Int, unit: Int): List<String> {
//        var gap = today - avgSec
//        var gapMessage: String = ""
//        var middleMessage: String = ""
//        var conMessage: String = ""
//
//        if (gap > 0) {
//            // 현재 누적 공부시간이 더 많은 경우
//            gapMessage = clockformat.calSecToKorean(gap)
//            middleMessage = getString(R.string.statistic_graph_middle_moreless)
//            conMessage = getString(R.string.statistic_graph_more)
//        } else if (gap == 0) {
//            // 현재 누적 시간과 이달 평균 시간이 같은 경우
//            gapMessage = ""
//            middleMessage = getString(R.string.statistic_graph_middle_same)
//            conMessage = getString(R.string.statistic_graph_same)
//        } else {
//            // 이달 평균 시간이 더 많은 경우
//            gap = Math.abs(gap)
//            gapMessage = calSecToKorean(gap)
//            middleMessage = getString(R.string.statistic_graph_middle_moreless)
//            conMessage = getString(R.string.statistic_graph_less)
//        }
//        var messages = listOf<String>()
//
//        return
//    }
}