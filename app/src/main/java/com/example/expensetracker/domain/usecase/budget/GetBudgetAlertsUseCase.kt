package com.example.expensetracker.domain.usecase.budget

import com.example.expensetracker.domain.model.BudgetAlert
import com.example.expensetracker.domain.repository.BudgetRepository
import com.example.expensetracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetBudgetAlertsUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(): List<BudgetAlert> {
        return budgetRepository.getBudgetAlerts()
    }
}