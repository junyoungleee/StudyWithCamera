package com.parklee.studywithcam

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.parklee.studywithcam.model.StudyDatabase
import com.parklee.studywithcam.model.shared.PreferenceUtil
import com.parklee.studywithcam.repository.DatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SWCapplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { StudyDatabase.getDatabase(this, applicationScope)}
    val repository by lazy { DatabaseRepository(database!!.studyDao())}

    companion object {
        lateinit var pref: PreferenceUtil
    }

    override fun onCreate() {
        super.onCreate()
        pref = PreferenceUtil(applicationContext)

        // 다크모드 비활성화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}