package com.example.expensetracker.domain.usecase.budget

import com.example.expensetracker.domain.model.BudgetSummary
import com.example.expensetracker.domain.repository.BudgetRepository
import javax.inject.Inject

class GetBudgetSummaryUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budgetId: Long): BudgetSummary? {
        return repository.getBudgetSummary(budgetId)
    }
}