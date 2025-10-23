package com.example.expensetracker.domain.usecase.expense

import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import java.time.LocalDateTime

class GetExpensesByDateRangeUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(start: LocalDateTime, end: LocalDateTime): List<Expense>? =
        repository.getExpensesByDateRange(start, end)
}