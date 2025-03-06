package com.example.infone.notification

import android.content.Context
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

class NotificationScheduler (private val context: Context) {

    private var currentWorkId: UUID = UUID.randomUUID()

    private fun cancelCurrentSchedule() {
        WorkManager.getInstance(context).cancelWorkById(currentWorkId)
    }

    fun scheduleNotification(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = calendar.timeInMillis - now

        currentWorkId = UUID.randomUUID()

        val notificationWorkRequest: WorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setId(currentWorkId)
            .build()

        WorkManager.getInstance(context).enqueue(notificationWorkRequest)

        Log.d("MainActivity", "Work request created with ID: $currentWorkId, time: $hour:$minute")
    }

    fun rescheduleNotification(hour: Int, minute: Int) {
        cancelCurrentSchedule()
        scheduleNotification(hour, minute)
    }
}