package com.example.infone.notification;

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.infone.data.remote.RequestHelper
import com.example.infone.utils.Config
import com.example.infone.model.DataPoint
import com.example.infone.data.local.Preferences
import kotlinx.coroutines.runBlocking

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val preferences = Preferences(context)

    override fun doWork(): Result {
        return runBlocking {
            val dataPoints = RequestHelper().fetchDataPoints(Config.getURL() + "/datapoints" + preferences.getIds().joinToString(",", prefix = "/", postfix = ""))
            showNotification(dataPoints)
            Result.success()
        }
    }

    private fun showNotification(dataPoints: List<DataPoint>) {
        val notificationMessage = DataPoint.listToNotificationMessage(dataPoints)
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        val notification = NotificationCompat.Builder(applicationContext, "Notification_Channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Update")
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}
