package com.parklee.studywithcam.model.dao

import androidx.room.*
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study
import kotlinx.coroutines.flow.Flow

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


    // QUERY ---------------------------------------------------------------------
    // 캘린더 데이터 받아오는 쿼리
    @Query("SELECT * FROM DailyStudy WHERE date LIKE :yearMonth || '%'")
    fun getCalendarState(yearMonth: String): Flow<List<DailyStudy>>

    // 어제 누적 시간 받아오는 쿼리
    @Query("SELECT time FROM DailyStudy WHERE date = :yesterday")
    fun getYesterdayTime(yesterday: String): Flow<Int>

    // 오늘 산만 구간 받아오는 쿼리
    @Query("SELECT * FROM Disturb WHERE date = :today AND type = :type")
    suspend fun getTodayDisturbSections(today: String, type: String): List<Disturb>

    // 오늘의 공부 구간 받아오는 쿼리
    @Query("SELECT * FROM Study WHERE date = :today")
    suspend fun getTodayStudySections(today: String): List<Study>

    @Query("SELECT time FROM DailyStudy WHERE date = :today")
    fun getTodayRealStudyTime(today: String): Flow<Int>

    @Query("SELECT SUM(time) FROM Disturb WHERE date = :today AND type = :type")
    fun getTodayDisturbTypeTime(today: String, type: String): Flow<Int>

}