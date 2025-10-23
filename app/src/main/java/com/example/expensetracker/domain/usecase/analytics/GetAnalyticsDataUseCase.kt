package com.example.expensetracker.domain.usecase.analytics

import com.example.expensetracker.domain.model.AnalyticsData
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.domain.repository.TimeRange
import javax.inject.Inject

class GetAnalyticsDataUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(timeRange: TimeRange): AnalyticsData {
        return repository.getAnalyticsData(timeRange)
    }
}