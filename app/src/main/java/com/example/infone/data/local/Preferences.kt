package com.example.infone.data.local

import android.content.Context
import android.util.Log
import androidx.work.WorkManager

class Preferences(private val context: Context) {

    companion object {
        const val PREFS_NAME = "InfonePrefs"
        const val IDS_PREF = "InfoneIds"
        const val NOTIF_TIME_PREF = "InfoneTime"
    }

    fun addId(id: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentIds = sharedPreferences.getStringSet(IDS_PREF, emptySet()) ?: emptySet()
        val updatedIds = currentIds + id.toString()
        with(sharedPreferences.edit()) {
            putStringSet(IDS_PREF, updatedIds)
            apply()
        }
    }

    fun removeId(id: Int) {
        Log.d("RemoveId", "Prefs before remove: ${getIds()}")
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentIds = sharedPreferences.getStringSet(IDS_PREF, emptySet()) ?: emptySet()
        val updatedIds = currentIds - id.toString()
        with(sharedPreferences.edit()) {
            putStringSet(IDS_PREF, updatedIds)
            apply()
        }
        Log.d("RemoveId", "Prefs after remove: ${getIds()}")
    }

    fun getIds(): List<Int> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val ids = sharedPreferences.getStringSet(IDS_PREF, emptySet()) ?: emptySet()
        return ids.map { it.toInt() }
    }

    fun saveNotificationTime(time: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        WorkManager.getInstance(context).cancelAllWork()
        editor.putInt(NOTIF_TIME_PREF, time)
        editor.apply()
    }

    fun getNotificationTime(): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        Log.d("NotifTime", "Time: ${sharedPreferences.getInt(NOTIF_TIME_PREF, 8)}")
        return sharedPreferences.getInt(NOTIF_TIME_PREF, 8)
    }
}