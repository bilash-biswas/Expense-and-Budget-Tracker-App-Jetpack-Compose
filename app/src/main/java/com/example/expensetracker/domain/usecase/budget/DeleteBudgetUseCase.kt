package com.example.expensetracker.domain.usecase.budget

import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.repository.BudgetRepository
import javax.inject.Inject

class DeleteBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) = repository.deleteBudget(budget)
}