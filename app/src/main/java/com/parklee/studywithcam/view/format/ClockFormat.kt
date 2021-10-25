package com.parklee.studywithcam.view.format

class ClockFormat {
    // 타이머 String 생성
    fun calSecToString(time: Int): String {
        var hour = time / 3600
        var min = (time - (hour*3600)) / 60
        var sec = (time - (hour*3600)) % 60
        return "${makeText(hour)} : ${makeText(min)} : ${makeText(sec)}"
    }

    private fun makeText(t: Int): String {
        if (t < 10) return "0${t}" else return "$t"
    }
}