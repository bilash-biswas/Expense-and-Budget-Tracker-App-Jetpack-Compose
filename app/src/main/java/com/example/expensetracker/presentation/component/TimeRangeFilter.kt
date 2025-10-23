package com.example.expensetracker.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.expensetracker.domain.repository.TimeRange

@Composable
fun TimeRangeFilter(
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = when(selectedTimeRange) {
                    TimeRange.LAST_30_DAYS -> "30 Days"
                    TimeRange.LAST_3_MONTHS -> "3 Months"
                    TimeRange.LAST_6_MONTHS -> "6 Months"
                    TimeRange.LAST_YEAR -> "1 Year"
                    TimeRange.CUSTOM -> "Custom"
                }
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select time range"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TimeRange.entries.forEach { range ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = when(range) {
                                TimeRange.LAST_30_DAYS -> "Last 30 Days"
                                TimeRange.LAST_3_MONTHS -> "Last 3 Months"
                                TimeRange.LAST_6_MONTHS -> "Last 6 Months"
                                TimeRange.LAST_YEAR -> "Last Year"
                                TimeRange.CUSTOM -> "Custom"
                            }
                        )
                            },
                    onClick = {
                        onTimeRangeSelected(range)
                        expanded = false
                    }
                )
            }
        }
    }
}