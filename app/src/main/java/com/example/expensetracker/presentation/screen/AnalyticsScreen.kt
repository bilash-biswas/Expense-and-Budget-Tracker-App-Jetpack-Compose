package com.example.expensetracker.presentation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.example.expensetracker.presentation.component.AnalyticsShimmer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.domain.model.SpendingInsights
import com.example.expensetracker.presentation.component.MonthlyComparisonSection
import com.example.expensetracker.presentation.component.TimeRangeFilter
import com.example.expensetracker.presentation.component.charts.CategoryDistributionChart
import com.example.expensetracker.presentation.component.charts.MonthlyTrendsChart
import com.example.expensetracker.presentation.component.charts.WeeklySpendingChart
import com.example.expensetracker.presentation.viewmodel.AnalyticsState
import com.example.expensetracker.presentation.viewmodel.AnalyticsViewModel
import com.example.expensetracker.util.format
import com.google.android.material.loadingindicator.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val analyticsState by viewModel.analyticsState.collectAsState()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        TimeRangeFilter(
                            selectedTimeRange = selectedTimeRange,
                            onTimeRangeSelected = { viewModel.loadAnalyticsData(it) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Analytics Content
            if (analyticsState.isLoading) {
                AnalyticsShimmer()
            } else if (analyticsState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error Loading Analytics",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = analyticsState.error ?: "Unknown error occurred",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                AnalyticsContent(
                    analyticsState = analyticsState,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun AnalyticsContent(
    analyticsState: AnalyticsState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        // Key Metrics
        analyticsState.analyticsData?.spendingInsights?.let { insights ->
            KeyMetricsSection(insights = insights)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Monthly Trends Chart
        Log.d("AnalyticsScreen", "analyticsState.monthlyTrends: ${analyticsState.monthlyTrends}")
        analyticsState.analyticsData?.monthlyTrends.takeIf { it!!.isNotEmpty() }?.let { trends ->
            MonthlyTrendsChart(monthlyTrends = trends)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Distribution
        analyticsState.analyticsData?.categoryDistribution?.let { distribution ->
            CategoryDistributionChart(categoryDistribution = distribution)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly Spending
        analyticsState.analyticsData?.weeklySpending?.let { weeklySpending ->
            WeeklySpendingChart(weeklySpending = weeklySpending)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Comparison Data
        analyticsState.analyticsData?.comparisonData?.let { comparison ->
            MonthlyComparisonSection(comparisonData = comparison)
        }
    }
}

@Composable
fun KeyMetricsSection(insights: SpendingInsights) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Key Metrics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                KeyMetricItem(
                    title = "Total Spent",
                    value = "$${insights.totalSpent.format(2)}",
                    subtitle = "This period"
                )

                Spacer(modifier = Modifier.width(16.dp))

                KeyMetricItem(
                    title = "Daily Average",
                    value = "$${insights.spendingVelocity.format(2)}",
                    subtitle = "Per day"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                KeyMetricItem(
                    title = "Monthly Average",
                    value = "$${insights.averageMonthlySpending.format(2)}",
                    subtitle = "All time"
                )

                Spacer(modifier = Modifier.width(16.dp))

                KeyMetricItem(
                    title = "Savings Potential",
                    value = "$${insights.savingsPotential.format(2)}",
                    subtitle = "Possible savings"
                )
            }
        }
    }
}

@Composable
fun KeyMetricItem(title: String, value: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}