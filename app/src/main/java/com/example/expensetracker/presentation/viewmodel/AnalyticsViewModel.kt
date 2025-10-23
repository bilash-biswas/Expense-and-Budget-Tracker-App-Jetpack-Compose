package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.AnalyticsData
import com.example.expensetracker.domain.model.MonthlyTrend
import com.example.expensetracker.domain.repository.TimeRange
import com.example.expensetracker.domain.usecase.analytics.GetAnalyticsDataUseCase
import com.example.expensetracker.domain.usecase.analytics.GetMonthlyTrendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsDataUseCase: GetAnalyticsDataUseCase,
    private val getMonthlyTrendsUseCase: GetMonthlyTrendsUseCase
) : ViewModel() {

    private val _analyticsState = MutableStateFlow(AnalyticsState())
    val analyticsState: StateFlow<AnalyticsState> = _analyticsState

    private val _selectedTimeRange = MutableStateFlow(TimeRange.LAST_30_DAYS)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange

    init {
        loadAnalyticsData(TimeRange.LAST_30_DAYS)
    }

    fun loadAnalyticsData(timeRange: TimeRange) {
        _selectedTimeRange.value = timeRange
        _analyticsState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val analyticsData = getAnalyticsDataUseCase(timeRange)
                _analyticsState.update {
                    it.copy(
                        analyticsData = analyticsData,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _analyticsState.update {
                    it.copy(
                        error = e.message ?: "Failed to load analytics",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadMonthlyTrends(months: Int) {
        viewModelScope.launch {
            try {
                val trends = getMonthlyTrendsUseCase(months)
                _analyticsState.update { it.copy(monthlyTrends = trends) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class AnalyticsState(
    val analyticsData: AnalyticsData? = null,
    val monthlyTrends: List<MonthlyTrend> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)