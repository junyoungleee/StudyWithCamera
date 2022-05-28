package com.parklee.studywithcam.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.parklee.studywithcam.model.dao.StudyDAO
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study
import kotlinx.coroutines.CoroutineScope

@Database(entities = arrayOf(DailyStudy::class, Study::class, Disturb::class), version = 2)
abstract class StudyDatabase: RoomDatabase() {
    abstract fun studyDao(): StudyDAO

    companion object {
        private var INSTANCE: StudyDatabase? = null

        fun getDatabase(context: Context): StudyDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "study-database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE StudyAlter(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, startTime TEXT NOT NULL, endTime TEXT NOT NULL, time INTEGER NOT NULL)")
                database.execSQL("DROP TABLE Study")
                database.execSQL("ALTER TABLE StudyAlter RENAME TO Study")

                database.execSQL("CREATE TABLE DisturbAlter(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, type TEXT NOT NULL, startTime TEXT NOT NULL, endTime TEXT NOT NULL, time INTEGER NOT NULL)")
                database.execSQL("DROP TABLE Disturb")
                database.execSQL("ALTER TABLE DisturbAlter RENAME TO Disturb")
            }
        }
    }
}