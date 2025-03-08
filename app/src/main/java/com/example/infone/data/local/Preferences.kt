package com.example.infone.data.local

import android.content.Context
import android.util.Log
import androidx.work.WorkManager

class Preferences(private val context: Context) {

    companion object {
        const val PREFS_NAME = "InfonePrefs"
        const val IDS_PREF = "InfoneIds"
        const val NOTIF_HOUR_PREF = "InfoneNotifHour"
        const val NOTIF_MINUTE_PREF = "InfoneNotifMinute"
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

    fun saveNotificationTime(hour: Int, minute: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        WorkManager.getInstance(context).cancelAllWork()
        editor.putInt(NOTIF_HOUR_PREF, hour)
        editor.putInt(NOTIF_MINUTE_PREF, minute)
        editor.apply()
    }

    fun getNotificationHour(): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(NOTIF_HOUR_PREF, 8)
    }

    fun getNotificationMinute(): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(NOTIF_MINUTE_PREF, 0)
    }
}