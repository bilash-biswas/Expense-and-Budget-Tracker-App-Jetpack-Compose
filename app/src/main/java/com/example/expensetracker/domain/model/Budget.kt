package com.example.expensetracker.domain.model

import java.time.LocalDateTime

data class Budget(
    val id: Long = 0,
    val category: ExpenseCategory,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?,
    val isActive: Boolean,
    val notificationEnabled: Boolean = true,
    val alertThreshold: Double = 0.8
)

enum class BudgetPeriod {
    WEEKLY, MONTHLY, QUARTERLY, YEARLY
}

data class BudgetAlert(
    val budget: Budget,
    val currentSpending: Double,
    val percentageUsed: Double,
    val isOverBudget: Boolean,
    val daysRemaining: Int
)

data class BudgetSummary(
    val budget: Budget,
    val currentSpending: Double,
    val remainingAmount: Double,
    val percentageUsed: Double,
    val dailyBudget: Double,
    val projectedOverspend: Boolean
)