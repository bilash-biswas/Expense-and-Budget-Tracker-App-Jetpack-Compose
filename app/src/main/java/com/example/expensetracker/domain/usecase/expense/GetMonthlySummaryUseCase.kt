package com.example.expensetracker.domain.usecase.expense

import com.example.expensetracker.domain.repository.ExpenseRepository

class GetMonthlySummaryUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(month: Int, year: Int) = repository.getMonthlySummary(month, year)
}