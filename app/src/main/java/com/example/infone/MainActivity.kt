package com.example.infone

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infone.data.local.Preferences
import com.example.infone.data.remote.RequestHelper
import com.example.infone.model.DataPoint
import com.example.infone.notification.NotificationScheduler
import com.example.infone.utils.Config
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val preferences: Preferences by lazy { Preferences(applicationContext) }
    private val notificationScheduler by lazy { NotificationScheduler(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Config.loadConfig(applicationContext)
        createNotificationChannel()
        //createWorkRequest(preferences.getNotifTime(), 24)
        notificationScheduler.scheduleNotification(preferences.getNotificationHour(), preferences.getNotificationMinute())

        setContent {
            AppUI()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Notification_Channel",
                "Channel for periodic notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("DefaultLocale")
    @Composable
    fun AppUI() {
        val dataPoints = remember { mutableStateListOf<DataPoint>() }
        val selectedItems = remember { mutableStateMapOf<Int, Boolean>() }
        val context = LocalContext.current
        var showTimePicker by remember { mutableStateOf(false) }
        var selectedTime by remember { mutableStateOf("Not set") }
        var selectedCount by remember { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            val savedIds = preferences.getIds().toSet()
            val data = RequestHelper().fetchDataPoints(Config.url + "/datapoints")
            dataPoints.clear()
            dataPoints.addAll(data)
            data.forEach { selectedItems[it.id] = it.id in savedIds }
        }

        val backgroundColor = Color(0xFF121212)
        val textColor = Color(0xFFE0E0E0)
        val accentColor = Color(0xFFBB86FC)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Infone", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(dataPoints) { dataPoint ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                    ) {
                        Checkbox(
                            checked = selectedItems[dataPoint.id] ?: false,
                            onCheckedChange = { isChecked ->
                                selectedItems[dataPoint.id] = isChecked
                                if (isChecked) {
                                    Log.d("MainActivity", "Adding ID: ${dataPoint.id}")
                                    preferences.addId(dataPoint.id)
                                    selectedCount++
                                } else {
                                    Log.d("MainActivity", "Removing ID: ${dataPoint.id}")
                                    preferences.removeId(dataPoint.id)
                                    selectedCount--
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = accentColor, uncheckedColor = textColor)
                        )
                        Text(text = dataPoint.name, fontSize = 16.sp, color = textColor, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Choose Notification Time",
                        tint = textColor
                    )
                }
            }

            if (showTimePicker) {
                val calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        showTimePicker = false
                        val newTime = String.format("%02d:%02d", hour, minute)
                        if (selectedTime != newTime) {
                            selectedTime = newTime
                            Log.d("MainActivity", "New time selected: $newTime")
                            preferences.saveNotificationTime(hour, minute)
                            //WorkManager.getInstance(context).cancelWorkById(workId)
                            //createWorkRequest(hourOfDay, minute)
                            notificationScheduler.rescheduleNotification(hour, minute)
                        } else {
                            Log.d("MainActivity", "Time not changed")
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )

                timePickerDialog.setOnCancelListener {
                    showTimePicker = false
                    Log.d("MainActivity", "Time picker canceled")
                }
                timePickerDialog.show()
            }
        }
    }

}
