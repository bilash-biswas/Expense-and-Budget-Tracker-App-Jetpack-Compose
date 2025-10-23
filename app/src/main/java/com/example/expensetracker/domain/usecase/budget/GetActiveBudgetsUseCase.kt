package com.example.expensetracker.domain.usecase.budget

import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.repository.BudgetRepository
import javax.inject.Inject

class GetActiveBudgetsUseCase @Inject constructor(
    val repository: BudgetRepository
) {
    suspend operator fun invoke(): List<Budget> = repository.getActiveBudgets()
}