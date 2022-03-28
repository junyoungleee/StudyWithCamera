package com.parklee.studywithcam.view.graph

import android.content.Context
import androidx.core.content.ContextCompat
import com.parklee.studywithcam.R
import com.parklee.studywithcam.model.entity.DailyStudy
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class ColorCalendar(context: Context, dailyStudy: DailyStudy) : DayViewDecorator {

    var thisDay = dailyStudyToCalendarDay(dailyStudy.date)

    val level1 = ContextCompat.getDrawable(context, R.drawable.calendar_day_background)!!
    val level2 = ContextCompat.getDrawable(context, R.drawable.calendar_day_background2)!!
    val level3 = ContextCompat.getDrawable(context, R.drawable.calendar_day_background3)!!
    val level4 = ContextCompat.getDrawable(context, R.drawable.calendar_day_background4)!!
    val level5 = ContextCompat.getDrawable(context, R.drawable.calendar_day_background5)!!

    val studyTime = dailyStudy.time

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == thisDay
    }

    override fun decorate(view: DayViewFacade?) {
        // 공부한 시간별 컬러 지정
        when {
            studyTime <= 10800 -> {
                view!!.setBackgroundDrawable(level1)
            }
            studyTime in 10801..21600 -> {
                view!!.setBackgroundDrawable(level2)
            }
            studyTime in 21600..32400 -> {
                view!!.setBackgroundDrawable(level3)
            }
            studyTime in 32500..43200 -> {
                view!!.setBackgroundDrawable(level4)
            }
            studyTime > 43200 -> {
                view!!.setBackgroundDrawable(level5)
            }
        }
    }

    private fun dailyStudyToCalendarDay(daily: String): CalendarDay {
        val year = Integer.parseInt("20" + daily.substring(0, 2))
        val month = Integer.parseInt(daily.substring(2, 4))
        val day = Integer.parseInt(daily.substring(4))

        return CalendarDay.from(year, month, day)
    }

}