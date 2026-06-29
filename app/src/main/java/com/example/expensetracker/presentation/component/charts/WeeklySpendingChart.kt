package com.example.expensetracker.presentation.component.charts

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    val barColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Spending Pattern",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (weeklySpending.isNotEmpty()) {
                val maxAmount = weeklySpending.maxOfOrNull { it.totalAmount } ?: 0.0
                val steps = 5
                val stepValue = maxAmount / steps

                val barData = weeklySpending.mapIndexed { index, week ->
                    BarData(
                        point = Point(
                            x = index.toFloat(),
                            y = week.totalAmount.toFloat()
                        ),
                        color = barColor,
                        label = "W${index + 1}",
                        description = "Week ${index + 1}: $${week.totalAmount.format(2)}"
                    )
                }

                // Create X-axis data
                val xAxisData = AxisData.Builder()
                    .axisStepSize(45.dp)
                    .steps(barData.size - 1)
                    .labelData { index ->
                        "W${index + 1}"
                    }
                    .axisLineColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                    .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
                    .build()

                // Create Y-axis data
                val yAxisData = AxisData.Builder()
                    .steps(steps)
                    .labelAndAxisLinePadding(20.dp)
                    .axisOffset(20.dp)
                    .labelData { index ->
                        val amount = index * stepValue
                        "$${amount.toInt()}"
                    }
                    .axisLineColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                    .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No weekly data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}