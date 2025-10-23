package com.example.expensetracker.domain.repository

import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.model.BudgetAlert
import com.example.expensetracker.domain.model.BudgetSummary
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgets(): Flow<List<Budget>>
    suspend fun getBudgetById(id: Long): Budget?
    suspend fun insertBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
    suspend fun getBudgetAlerts(): List<BudgetAlert>
    suspend fun getBudgetSummary(budgetId: Long): BudgetSummary?
    suspend fun getActiveBudgets(): List<Budget>
}