package com.parklee.studywithcam.repository

import androidx.lifecycle.LiveData
import com.parklee.studywithcam.model.dao.StudyDAO
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


class DatabaseRepository(private val studyDao: StudyDAO) {

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

    // 리포트 페이지 ------------------------------------------------------------
    // 캘린더 데이터 받아오는 메서드
    fun getCalendarState(): Flow<List<DailyStudy>> {
        val today = LocalDate.now().toString()
        val thisMonth = today.substring(0, 7)  // yyyy-MM

        return studyDao.getCalendarState(thisMonth)
    }

    // 어제 누적 시간 받아오는 메서드
    fun getYesterdayTime(): Flow<Int> {
        val yesterday = LocalDate.now().minusDays(1)
        return studyDao.getYesterdayTime("$yesterday")
    }

    suspend fun getTodayDisturbSections(type: String): List<Disturb> {
        val today = LocalDate.now().toString()
        return studyDao.getTodayDisturbSections(today, type)
    }

    suspend fun getTodayStudySections(): List<Study> {
        val today = LocalDate.now().toString()
        return studyDao.getTodayStudySections(today)
    }

    fun getTodayRealStudyTime(): Flow<Int> {
        val today = LocalDate.now().toString()
        return studyDao.getTodayRealStudyTime(today)
    }

    fun getTodayDisturbTypeTime(type: String): Flow<Int> {
        val today = LocalDate.now().toString()
        return studyDao.getTodayDisturbTypeTime(today, type)
    }

}