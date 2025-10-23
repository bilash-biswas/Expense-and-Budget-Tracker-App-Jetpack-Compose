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
    Card(
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Monthly Spending Trends",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (monthlyTrends.isNotEmpty()) {
                val maxAmount = monthlyTrends.maxOf { it.totalAmount }

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
                    .axisStepSize(50.dp)
                    .steps(points.size - 1)
                    .labelData { index ->
                        if (index < xAxisLabels.size) xAxisLabels[index] else ""
                    }
                    .build()

                // Create Y-axis data
                val yAxisData = AxisData.Builder()
                    .steps(5)
                    .labelAndAxisLinePadding(20.dp)
                    .labelData { value ->
                        "$${value.toInt()}" // Format as currency
                    }
                    .build()

                val lineChartData = LineChartData(
                    linePlotData = LinePlotData(
                        lines = listOf(
                            Line(
                                dataPoints = points,
                                lineStyle = LineStyle(
                                    lineType = LineType.SmoothCurve(isDotted = false)
                                ),
                                selectionHighlightPoint = SelectionHighlightPoint(
                                    color = Color(0xFF6750A4)
                                )
                            )
                        )
                    ),
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    gridLines = co.yml.charts.ui.linechart.model.GridLines(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )

                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    lineChartData = lineChartData
                )
            } else {
                // Show empty state
                Text(
                    text = "No monthly trend data available",
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