package com.example.expensetracker.presentation.component.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.common.model.Point
import com.example.expensetracker.domain.model.WeeklySpending
import com.example.expensetracker.util.format

@Composable
fun WeeklySpendingChart(weeklySpending: List<WeeklySpending>) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Spending Pattern",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (weeklySpending.isNotEmpty()) {
                val maxAmount = weeklySpending.maxOf { it.totalAmount }

                // Create bar data with Point objects
                val barData = weeklySpending.mapIndexed { index, week ->
                    BarData(
                        point = Point(
                            x = index.toFloat(), // X position (week index)
                            y = week.totalAmount.toFloat() // Y value (amount)
                        ),
                        color = Color(0xFF4ECDC4),
                        label = "W${index + 1}",
                        description = "Week ${index + 1}: $${week.totalAmount.format(2)}"
                    )
                }

                // Create X-axis data
                val xAxisData = AxisData.Builder()
                    .axisStepSize(30.dp)
                    .steps(barData.size - 1)
                    .labelData { index ->
                        "W${index + 1}" // Week labels: W1, W2, W3, etc.
                    }
                    .build()

                // Create Y-axis data
                val yAxisData = AxisData.Builder()
                    .steps(5)
                    .labelAndAxisLinePadding(20.dp)
                    .axisOffset(20.dp)
                    .labelData { value ->
                        "$${value.toInt()}" // Format as currency
                    }
                    .build()

                val barStyle = BarStyle(
                    barWidth = 20.dp,
                    paddingBetweenBars = 15.dp,
                )

                val barChartData = BarChartData(
                    chartData = barData,
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    barStyle = barStyle
                )

                BarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    barChartData = barChartData
                )
            } else {
                // Show empty state
                Text(
                    text = "No weekly data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                )
            }
        }
    }
}