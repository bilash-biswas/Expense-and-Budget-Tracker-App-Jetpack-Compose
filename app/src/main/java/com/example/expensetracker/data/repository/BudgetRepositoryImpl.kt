package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.BudgetDao
import com.example.expensetracker.data.local.dao.BudgetWithSpending
import com.example.expensetracker.data.local.entity.toBudget
import com.example.expensetracker.data.local.entity.toEntity
import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.model.BudgetAlert
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.BudgetSummary
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.domain.repository.BudgetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): BudgetRepository{
    override fun getBudgets(): Flow<List<Budget>> {
        return budgetDao.getBudgets().map { entities ->
            entities.map { it.toBudget() }
        }
    }

    override suspend fun getBudgetById(id: Long): Budget? {
        return budgetDao.getBudgetById(id)?.toBudget()
    }

    override suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }

    override suspend fun getBudgetAlerts(): List<BudgetAlert> {
        val budgetsWithSpending = budgetDao.getBudgetsWithCurrentSpending()

        return budgetsWithSpending.mapNotNull { budgetWithSpending ->
            val budget = budgetWithSpending.toBudget()
            val percentageUsed = budgetWithSpending.currentSpending / budget.amount

            if (percentageUsed >= budget.alertThreshold) {
                BudgetAlert(
                    budget = budget,
                    currentSpending = budgetWithSpending.currentSpending,
                    percentageUsed = percentageUsed,
                    isOverBudget = budgetWithSpending.currentSpending > budget.amount,
                    daysRemaining = calculateDaysRemaining(budget)
                )
            } else {
                null
            }
        }
    }

    override suspend fun getBudgetSummary(budgetId: Long): BudgetSummary? {
        val budget = getBudgetById(budgetId) ?: return null
        val utilization = budgetDao.getBudgetUtilization().find { it.category == budget.category.name }

        return BudgetSummary(
            budget = budget,
            currentSpending = utilization?.spentAmount ?: 0.0,
            remainingAmount = budget.amount - (utilization?.spentAmount ?: 0.0),
            percentageUsed = if (budget.amount > 0) (utilization?.spentAmount ?: 0.0) / budget.amount else 0.0,
            dailyBudget = calculateDailyBudget(budget),
            projectedOverspend = isProjectedToOverspend(budget, utilization?.spentAmount ?: 0.0)
        )
    }

    override suspend fun getActiveBudgets(): List<Budget> {
        return budgetDao.getActiveBudgets().map { it.toBudget() }
    }

    private fun calculateDailyBudget(budget: Budget): Double {
        val daysInPeriod = when (budget.period) {
            BudgetPeriod.WEEKLY -> 7
            BudgetPeriod.MONTHLY -> 30 // Approximation
            BudgetPeriod.QUARTERLY -> 90
            BudgetPeriod.YEARLY -> 365
        }
        return budget.amount / daysInPeriod
    }

    private fun calculateDaysRemaining(budget: Budget): Int {
        val now = LocalDateTime.now()
        return when (budget.period) {
            BudgetPeriod.WEEKLY -> {
                val endOfWeek = now.with(java.time.DayOfWeek.SUNDAY)
                java.time.temporal.ChronoUnit.DAYS.between(now, endOfWeek).toInt()
            }
            BudgetPeriod.MONTHLY -> {
                val endOfMonth = now.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())
                java.time.temporal.ChronoUnit.DAYS.between(now, endOfMonth).toInt()
            }
            BudgetPeriod.QUARTERLY -> {
                val currentQuarter = (now.monthValue - 1) / 3 + 1
                val endOfQuarter = now.withMonth(currentQuarter * 3).with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())
                java.time.temporal.ChronoUnit.DAYS.between(now, endOfQuarter).toInt()
            }
            BudgetPeriod.YEARLY -> {
                val endOfYear = now.withMonth(12).withDayOfMonth(31)
                java.time.temporal.ChronoUnit.DAYS.between(now, endOfYear).toInt()
            }
        }
    }

    private fun isProjectedToOverspend(budget: Budget, currentSpending: Double): Boolean {
        val daysPassed = calculateDaysPassed(budget)
        val totalDays = when (budget.period) {
            BudgetPeriod.WEEKLY -> 7
            BudgetPeriod.MONTHLY -> 30
            BudgetPeriod.QUARTERLY -> 90
            BudgetPeriod.YEARLY -> 365
        }
        val projectedSpending = (currentSpending / daysPassed) * totalDays
        return projectedSpending > budget.amount
    }

    private fun calculateDaysPassed(budget: Budget): Int {
        val now = LocalDateTime.now()
        val periodStart = when (budget.period) {
            BudgetPeriod.WEEKLY -> now.with(java.time.DayOfWeek.MONDAY)
            BudgetPeriod.MONTHLY -> now.withDayOfMonth(1)
            BudgetPeriod.QUARTERLY -> {
                val quarterStartMonth = ((now.monthValue - 1) / 3) * 3 + 1
                now.withMonth(quarterStartMonth).withDayOfMonth(1)
            }
            BudgetPeriod.YEARLY -> now.withDayOfYear(1)
        }
        return java.time.temporal.ChronoUnit.DAYS.between(periodStart, now).toInt() + 1
    }
}

private fun BudgetWithSpending.toBudget(): Budget {
    return Budget(
        id = id,
        category = ExpenseCategory.valueOf(category),
        amount = amount,
        period = BudgetPeriod.valueOf(period),
        startDate = LocalDateTime.parse(startDate, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        endDate = endDate.let { LocalDateTime.parse(it, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME) },
        isActive = isActive,
        notificationEnabled = notificationEnabled,
        alertThreshold = alertThreshold
    )
}