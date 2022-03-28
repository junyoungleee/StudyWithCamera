package com.parklee.studywithcam.model.shared

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    // nTime : 현재 공부시간
    // cTime : 오늘 누적시간
    // uid : 사용자 uid

    private val prefs: SharedPreferences = context.getSharedPreferences("timePrefs", 0)

    fun getPrefTime(key: String): Int {
        return prefs.getInt(key, 0).toInt()
    }

    fun setPrefTime(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getUid(key: String): String {
        return prefs.getString(key, "").toString()
    }

    fun setUid(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getWidth(key: String): Int {
        return prefs.getInt(key, 0).toInt()
    }

    fun setWidth(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getHeight(key: String): Int {
        return prefs.getInt(key, 0).toInt()
    }

    fun setHeight(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

}