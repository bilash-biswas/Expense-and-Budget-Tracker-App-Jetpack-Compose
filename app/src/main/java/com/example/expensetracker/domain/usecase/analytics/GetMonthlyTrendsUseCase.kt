package com.example.expensetracker.domain.usecase.analytics

import com.example.expensetracker.domain.model.MonthlyTrend
import com.example.expensetracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetMonthlyTrendsUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(months: Int): List<MonthlyTrend> {
        return repository.getMonthlyTrends(months)
    }
}