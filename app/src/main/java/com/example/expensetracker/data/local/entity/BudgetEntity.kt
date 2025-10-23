package com.example.expensetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.ExpenseCategory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val amount: Double,
    val period: String,
    val startDate: String,
    val endDate: String? = null,
    val isActive: Boolean = true,
    val notificationEnabled: Boolean = true,
    val alertThreshold: Double = 0.8
)

fun BudgetEntity.toBudget(): Budget {
    return Budget(
        id = id,
        category = ExpenseCategory.valueOf(category),
        amount = amount,
        period = BudgetPeriod.valueOf(period),
        startDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        endDate = endDate.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) },
        isActive = isActive,
        notificationEnabled = notificationEnabled,
        alertThreshold = alertThreshold
    )
}

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        category = category.name,
        amount = amount,
        period = period.name,
        startDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        endDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        isActive = isActive,
        notificationEnabled = notificationEnabled,
        alertThreshold = alertThreshold
    )
}