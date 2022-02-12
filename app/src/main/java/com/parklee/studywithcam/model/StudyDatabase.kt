package com.parklee.studywithcam.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.parklee.studywithcam.model.dao.StudyDAO
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(DailyStudy::class, Study::class, Disturb::class), version = 1)
abstract class StudyDatabase: RoomDatabase() {
    abstract fun studyDao(): StudyDAO

//    private class StudyDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    var studyDao = database.studyDao()
//                }
//            }
//        }
//    }

    companion object {
        private var INSTANCE: StudyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): StudyDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "study-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}