package com.example.expensetracker.domain.usecase.expense

import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.domain.repository.ExpenseRepository

class GetExpensesByCategoryUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(category: ExpenseCategory): List<Expense> =
        repository.getExpensesByCategory(category)
}