package com.parklee.studywithcam.view.format

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateAxisValueFormat : IndexAxisValueFormatter() {

    override fun getFormattedValue(value: Float): String {

        // Float(min) -> Date(mills)
        // days -> minutes
        var valueToMinutes = TimeUnit.MINUTES.toMillis(value.toLong())
        var timeMimutes = Date(valueToMinutes)
        var formatMinutes = SimpleDateFormat("HH:MM")

        return formatMinutes.format(timeMimutes)
    }
}