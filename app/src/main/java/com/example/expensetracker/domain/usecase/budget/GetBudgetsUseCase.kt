package com.example.expensetracker.domain.usecase.budget

import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val repository: BudgetRepository
){
    operator fun invoke(): Flow<List<Budget>> = repository.getBudgets()
}