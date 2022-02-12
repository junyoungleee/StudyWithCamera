package com.parklee.studywithcam.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study

@Dao
interface StudyDAO{
    // INSERT --------------------------------------------------------------------
    // 하루에 대한 누적 공부시간 → 시간 초기화시 Time=0으로 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayStudy(dailyStudy: DailyStudy)
    // 공부 구간
    @Insert
    suspend fun insertStudySection(studySection: Study)
    // 산만 구간
    @Insert
    suspend fun insertDisturbSection(disturbSection: Disturb)


//    // QUERY ---------------------------------------------------------------------
//    // 캘린더 데이터 받아오는 쿼리
//    @Query("")
//    suspend fun getCalendarState(month: String): LiveData<List<DailyStudy>>
//
//    // 어제 누적 시간 받아오는 쿼리
//    @Query("")
//    suspend fun getYesterdayTime(yesterday: String): LiveData<Int>
//
//    // 지난주 누적 시간 받아오는 쿼리
//    @Query("")
//    suspend fun getLastWeekTime(lastWeekStart: String, lastWeekEnd: String): LiveData<Int>
//
//    // 지난달(yyyy-MM) 누적 시간 받아오는 쿼리
//    @Query("")
//    suspend fun getLastMonthTime(month: String): LiveData<Int>
//
//    // 서버 DB 용 -----------------------------------------------------------------
//    @Query("")
//    suspend fun getTodayTime(): DailyStudy
//
//    // 오늘의 집중 구간 받아오는 쿼리
//    @Query("")
//    suspend fun getTodayStudySections(): List<Study>
//
//    // 오늘의 산만 구간 받아오는 쿼리
//    @Query("")
//    suspend fun getTodayDisturbSections(): List<Disturb>


}