package com.example.infone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infone.data.local.Preferences
import com.example.infone.data.remote.RequestHelper
import com.example.infone.model.DataPoint
import com.example.infone.notification.NotificationScheduler
import com.example.infone.ui.CustomTimePickerDialog
import com.example.infone.utils.Config

class MainActivity : ComponentActivity() {

    private val preferences: Preferences by lazy { Preferences(applicationContext) }
    private val notificationScheduler by lazy { NotificationScheduler(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Config.loadConfig(applicationContext)
        createNotificationChannel()
        notificationScheduler.scheduleNotification(preferences.getNotificationHour(), preferences.getNotificationMinute())
        enableEdgeToEdge()
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

    @Composable
    fun AppUI() {
        val dataPoints = remember { mutableStateListOf<DataPoint>() }
        val selectedItems = remember { mutableStateMapOf<Int, Boolean>() }
        val context = LocalContext.current
        var showTimePicker by remember { mutableStateOf(false) }
        var selectedTime by remember { mutableStateOf("Not set") }
        var showSelectedTimeSnackbar by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            val savedIds = preferences.getIds().toSet()
            val data = RequestHelper().fetchDataPoints(Config.getURL() + "/datapoints")
            dataPoints.clear()
            dataPoints.addAll(data)
            data.forEach { selectedItems[it.id] = it.id in savedIds }
        }

        val backgroundColor = Color(0xFF121212)
        val textColor = Color(0xFFA4A4A4)
        val accentColor = Color(0xFFBB86FC)
        val raleway = FontFamily(Font(R.font.raleway_variablefont_wghtt))
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.infone_logo)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                bitmap = logoBitmap.asImageBitmap(),
                contentDescription = "Infone Logo",
                tint = Color.Unspecified, // Fixed from previous issue
                modifier = Modifier.size(130.dp)
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(dataPoints) { dataPoint ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .background(
                                color = if (selectedItems[dataPoint.id] == true) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .then(
                                if (selectedItems[dataPoint.id] != true) {
                                    Modifier.border(
                                        width = 1.dp,
                                        color = accentColor.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable {
                                val isChecked = selectedItems[dataPoint.id] != true
                                selectedItems[dataPoint.id] = isChecked
                                if (isChecked) {
                                    Log.d("MainActivity", "Adding ID: ${dataPoint.id}")
                                    preferences.addId(dataPoint.id)
                                } else {
                                    Log.d("MainActivity", "Removing ID: ${dataPoint.id}")
                                    preferences.removeId(dataPoint.id)
                                }
                            }
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = dataPoint.name,
                                fontSize = 16.sp,
                                fontFamily = raleway,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedItems[dataPoint.id] == true) accentColor else textColor
                            )
                            Text(
                                text = dataPoint.description,
                                fontSize = 12.sp,
                                fontFamily = raleway,
                                fontWeight = FontWeight.Normal,
                                color = if (selectedItems[dataPoint.id] == true) accentColor.copy(alpha = 0.7f) else textColor.copy(alpha = 0.7f),
                                lineHeight = 14.sp
                            )
                        }
                        Icon(
                            imageVector = if (selectedItems[dataPoint.id] == true) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = "Enabled state",
                            tint = if (selectedItems[dataPoint.id] == true) accentColor else textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Choose Notification Time",
                        tint = textColor
                    )
                }
            }

            // Custom Time Picker Dialog
            if (showTimePicker) {
                CustomTimePickerDialog(
                    initialHour = preferences.getNotificationHour(),
                    initialMinute = preferences.getNotificationMinute(),
                    backgroundColor = backgroundColor,
                    textColor = textColor,
                    accentColor = accentColor,
                    fontFamily = raleway,
                    onDismiss = { showTimePicker = false },
                    onTimeSelected = { hour, minute ->
                        showSelectedTimeSnackbar = true
                        showTimePicker = false
                        val newTime = String.format("%02d:%02d", hour, minute)
                        if (selectedTime != newTime) {
                            selectedTime = newTime
                            Log.d("MainActivity", "New time selected: $newTime")
                            preferences.saveNotificationTime(hour, minute)
                            notificationScheduler.rescheduleNotification(hour, minute)
                        } else {
                            Log.d("MainActivity", "Time not changed")
                        }
                    }
                )
            }

            if(showSelectedTimeSnackbar) {
                Snackbar {
                    Text(
                        text = "Notification time set to $selectedTime",
                        color = accentColor,
                        fontFamily = raleway,
                        fontWeight = FontWeight.Bold,
                    )
                }
                // wait for 2 seconds before hiding the snackbar
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showSelectedTimeSnackbar = false
                }
            }
        }
    }
}
