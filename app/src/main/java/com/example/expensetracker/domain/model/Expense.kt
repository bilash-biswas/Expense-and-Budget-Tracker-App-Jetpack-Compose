package com.example.expensetracker.domain.model

import java.time.LocalDateTime

data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val date: LocalDateTime,
    val notes: String = "",
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType? = null
)

enum class ExpenseCategory(
    val displayName: String,
    val iconRes: String,
    val color: Long
) {
    FOOD("Food", "🍕", 0xFFFF6B6B),
    TRANSPORT("Transport", "🚗", 0xFF4ECDC4),
    ENTERTAINMENT("Entertainment", "🎬", 0xFF45B7D1),
    SHOPPING("Shopping", "🛍️", 0xFF96CEB4),
    BILLS("Bills", "📄", 0xFFFECA57),
    HEALTH("Health", "🏥", 0xFFFF9FF3),
    EDUCATION("Education", "📚", 0xFF54A0FF),
    OTHER("Other", "📦", 0xFF5F27CD)
}

enum class RecurrenceType {
    DAILY, WEEKLY, MONTHLY, YEARLY
}