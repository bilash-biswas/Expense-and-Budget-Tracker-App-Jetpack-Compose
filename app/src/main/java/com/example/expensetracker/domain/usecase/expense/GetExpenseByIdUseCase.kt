package com.example.expensetracker.domain.usecase.expense

import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository

class GetExpenseByIdUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: Long): Expense? = repository.getExpenseById(id)
}