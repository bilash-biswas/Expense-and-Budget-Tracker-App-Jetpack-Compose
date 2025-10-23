package com.example.expensetracker.domain.model

import com.example.expensetracker.domain.repository.MonthlySummary
import kotlinx.datetime.DayOfWeek
import java.time.LocalDateTime

data class AnalyticsData(
    val monthlyTrends: List<MonthlyTrend>,
    val categoryDistribution: List<CategoryDistribution>,
    val spendingInsights: SpendingInsights,
    val weeklySpending: List<WeeklySpending>,
    val comparisonData: ComparisonData
)

data class MonthlyTrend(
    val month: String,
    val year: Int,
    val totalAmount: Double,
    val averageDailySpending: Double,
    val highestSpendingDay: DailySpending?
)

data class CategoryDistribution(
    val category: ExpenseCategory,
    val amount: Double,
    val percentage: Double,
    val count: Int
)

data class SpendingInsights(
    val totalSpent: Double,
    val averageMonthlySpending: Double,
    val biggestExpense: Expense?,
    val mostFrequentCategory: ExpenseCategory?,
    val savingsPotential: Double,
    val spendingVelocity: Double
)

data class WeeklySpending(
    val weekStart: LocalDateTime,
    val weekEnd: LocalDateTime,
    val totalAmount: Double,
    val dailyAverages: Map<DayOfWeek, Double>
)

data class ComparisonData(
    val currentMonth: MonthlySummary,
    val previousMonth: MonthlySummary,
    val percentageChange: Double,
    val categoryChanges: Map<ExpenseCategory, Double>
)

data class DailySpending(
    val date: LocalDateTime,
    val amount: Double,
    val expenses: List<Expense>
)