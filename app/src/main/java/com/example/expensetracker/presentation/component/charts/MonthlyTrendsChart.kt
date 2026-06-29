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
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.common.model.Point
import com.example.expensetracker.domain.model.MonthlyTrend

@Composable
fun MonthlyTrendsChart(monthlyTrends: List<MonthlyTrend>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

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
                text = "Monthly Spending Trends",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (monthlyTrends.isNotEmpty()) {
                val maxAmount = monthlyTrends.maxOfOrNull { it.totalAmount } ?: 0.0
                val steps = 5
                val stepValue = maxAmount / steps

                val points = monthlyTrends.mapIndexed { index, trend ->
                    Point(
                        x = index.toFloat(),
                        y = trend.totalAmount.toFloat()
                    )
                }

                val xAxisLabels = monthlyTrends.map { trend ->
                    "${trend.month.take(3)} '${trend.year.toString().takeLast(2)}"
                }

                // Create X-axis data
                val xAxisData = AxisData.Builder()
                    .axisStepSize(55.dp)
                    .steps(points.size - 1)
                    .labelData { index ->
                        if (index < xAxisLabels.size) xAxisLabels[index] else ""
                    }
                    .axisLineColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                    .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
                    .build()

                // Create Y-axis data
                val yAxisData = AxisData.Builder()
                    .steps(steps)
                    .labelAndAxisLinePadding(20.dp)
                    .labelData { index ->
                        val amount = index * stepValue
                        "$${amount.toInt()}"
                    }
                    .axisLineColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                    .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
                    .build()

                val lineChartData = LineChartData(
                    linePlotData = LinePlotData(
                        lines = listOf(
                            Line(
                                dataPoints = points,
                                lineStyle = LineStyle(
                                    color = primaryColor,
                                    lineType = LineType.SmoothCurve(isDotted = false)
                                ),
                                selectionHighlightPoint = SelectionHighlightPoint(
                                    color = secondaryColor
                                )
                            )
                        )
                    ),
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    gridLines = co.yml.charts.ui.linechart.model.GridLines(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                )

                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    lineChartData = lineChartData
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No monthly trend data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}