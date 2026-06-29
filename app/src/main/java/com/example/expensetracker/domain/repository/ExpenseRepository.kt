package com.example.expensetracker.domain.repository

import com.example.expensetracker.domain.model.AnalyticsData
import com.example.expensetracker.domain.model.CategoryDistribution
import com.example.expensetracker.domain.model.ComparisonData
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.domain.model.MonthlyTrend
import com.example.expensetracker.domain.model.SpendingInsights
import com.example.expensetracker.domain.model.WeeklySpending
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ExpenseRepository {
    fun getExpenses(): Flow<List<Expense>>
    suspend fun getExpenseById(id: Long): Expense?
    suspend fun insertExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun getExpensesByDateRange(start: LocalDateTime, end: LocalDateTime): List<Expense>
    suspend fun getExpensesByCategory(category: ExpenseCategory): List<Expense>
    suspend fun getMonthlySummary(month: Int, year: Int): MonthlySummary


    suspend fun getAnalyticsData(timeRange: TimeRange): AnalyticsData
    suspend fun getCategoryDistribution(startDate: LocalDateTime, endDate: LocalDateTime): List<CategoryDistribution>
    suspend fun getMonthlyTrends(months: Int): List<MonthlyTrend>
    suspend fun getSpendingInsights(timeRange: TimeRange): SpendingInsights
    suspend fun getWeeklySpending(startDate: LocalDateTime, weeks: Int): List<WeeklySpending>
    suspend fun getComparisonData(): ComparisonData
}

enum class TimeRange {
    LAST_30_DAYS,
    LAST_3_MONTHS,
    LAST_6_MONTHS,
    LAST_YEAR,
    CUSTOM
}

data class MonthlySummary(
    val totalAmount: Double,
    val categoryBreakdown: Map<ExpenseCategory, Double>,
    val averageDailySpending: Double
)