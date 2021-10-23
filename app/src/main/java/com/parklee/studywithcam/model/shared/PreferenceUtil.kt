package com.parklee.studywithcam.model.shared

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("timePrefs", 0)

    fun getPrefTime(key: String): Int {
        return prefs.getInt(key, 0).toInt()
    }

    fun setPrefTime(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

}