package com.example.infone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CustomTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    backgroundColor: Color,
    textColor: Color,
    accentColor: Color,
    fontFamily: FontFamily,
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    var hour by remember { mutableIntStateOf(initialHour) }
    var minute by remember { mutableIntStateOf(initialMinute) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(300.dp)
                .background(backgroundColor, RoundedCornerShape(16.dp)),
            color = backgroundColor,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set Notification Time",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour Picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { hour = (hour + 1) % 24 }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Increase hour",
                                tint = accentColor
                            )
                        }
                        Text(
                            text = String.format("%02d", hour),
                            fontFamily = fontFamily,
                            fontSize = 32.sp,
                            color = accentColor
                        )
                        IconButton(onClick = { hour = if (hour - 1 < 0) 23 else hour - 1 }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Decrease hour",
                                tint = accentColor
                            )
                        }
                    }

                    Text(
                        text = ":",
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        color = textColor
                    )

                    // Minute Picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { minute = (minute + 1) % 60 }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Increase minute",
                                tint = accentColor
                            )
                        }
                        Text(
                            text = String.format("%02d", minute),
                            fontFamily = fontFamily,
                            fontSize = 32.sp,
                            color = accentColor
                        )
                        IconButton(onClick = { minute = if (minute - 1 < 0) 59 else minute - 1 }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Decrease minute",
                                tint = accentColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }
                    TextButton(onClick = { onTimeSelected(hour, minute) }) {
                        Text(
                            text = "Set",
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            color = accentColor
                        )
                    }
                }
            }
        }
    }
}