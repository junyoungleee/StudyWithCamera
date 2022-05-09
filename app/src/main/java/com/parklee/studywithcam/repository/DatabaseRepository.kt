package com.parklee.studywithcam.repository

import androidx.lifecycle.LiveData
import com.parklee.studywithcam.model.dao.StudyDAO
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study


class DatabaseRepository(private val studyDao: StudyDAO) {

    lateinit var calendarData: LiveData<List<DailyStudy>>
    lateinit var yesterdayTime: LiveData<Int>
    lateinit var lastWeekTime: LiveData<Int>
    lateinit var lastMonthTime: LiveData<Int>


    // 데이터 삽입 & 업데이트 ----------------------------------------------------
    suspend fun insertDayStudy(dailyStudy: DailyStudy){
        studyDao.insertDayStudy(dailyStudy)
    }

    suspend fun insertStudySection(studySection: Study) {
        studyDao.insertStudySection(studySection)
    }

    suspend fun insertDisturbSection(disturbSection: Disturb) {
        studyDao.insertDisturbSection(disturbSection)
    }



//    // 리포트 페이지 ------------------------------------------------------------
//    // 캘린더 데이터 받아오는 메서드
//    suspend fun getCalendarState(): LiveData<List<DailyStudy>> {
//        val today = LocalDate.now().toString()
//        val thisMonth = today.substring(0, 7)  // yyyy-MM
//
//        calendarData = studyDao.getCalendarState(thisMonth)
//        return calendarData
//    }
//
//    // 어제 누적 시간 받아오는 메서드
//    suspend fun getYesterdayTime(): LiveData<Int> {
//        val yesterday = LocalDate.now().minusDays(1)
//        yesterdayTime = studyDao.getYesterdayTime("$yesterday")
//        return yesterdayTime
//    }
//
//    // 지난주 누적 시간 받아오는 메서드
//    suspend fun getLastWeekTime(): LiveData<Int> {
//        val today = LocalDate.now()
//        val startDay = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
//        val endDay = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
//
//        lastWeekTime = studyDao.getLastWeekTime("$startDay", "$endDay")
//        return lastWeekTime
//    }
//
//    // 지난달 누적 시간 받아오는 메서드
//    suspend fun getLastMonthTime(): LiveData<Int> {
//        val today = LocalDate.now().toString()
//        val thisMonth = today.substring(0, 7)  // yyyy-MM
//
//        lastMonthTime = studyDao.getLastMonthTime("$thisMonth")
//        return lastMonthTime
//    }
}