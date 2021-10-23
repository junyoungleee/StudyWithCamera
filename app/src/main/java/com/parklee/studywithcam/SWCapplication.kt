package com.parklee.studywithcam

import android.app.Application
import com.parklee.studywithcam.model.shared.PreferenceUtil

class SWCapplication : Application() {

    companion object {
        lateinit var pref: PreferenceUtil
    }

    override fun onCreate() {
        super.onCreate()
        pref = PreferenceUtil(applicationContext)
    }
}