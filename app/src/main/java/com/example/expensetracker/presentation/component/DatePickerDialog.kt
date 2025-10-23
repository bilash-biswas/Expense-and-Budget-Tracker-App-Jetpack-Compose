// DatePickerDialog.kt
package com.example.expensetracker.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDateTime: LocalDateTime,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDateTime) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(initialDateTime) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = initialDateTime.hour,
        initialMinute = initialDateTime.minute
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.ofEpochMilli(millis)
                            selectedDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                                .withHour(initialDateTime.hour)
                                .withMinute(initialDateTime.minute)
                            showDatePicker = false
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.ofEpochMilli(millis)
                            val finalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                                .withHour(timePickerState.hour)
                                .withMinute(timePickerState.minute)
                            onDateSelected(finalDateTime)
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = true }
                ) {
                    Text("Back")
                }
            }
        ) {
            Column {
                Text(
                    text = "Select Time",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TimePicker(state = timePickerState)
            }
        }
    }
}