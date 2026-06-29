package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.domain.usecase.budget.CreateBudgetUseCase
import com.example.expensetracker.domain.usecase.budget.DeleteBudgetUseCase
import com.example.expensetracker.domain.usecase.budget.GetBudgetByIdUseCase
import com.example.expensetracker.domain.usecase.budget.GetBudgetsUseCase
import com.example.expensetracker.domain.usecase.budget.UpdateBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val getBudgetByIdUseCase: GetBudgetByIdUseCase,
    private val addBudgetUseCase: CreateBudgetUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase
) : ViewModel() {

    private val _budgets = getBudgetsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val budgets: StateFlow<List<Budget>> = _budgets

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val _currentBudget = MutableStateFlow<Budget?>(null)
    val currentBudget: StateFlow<Budget?> = _currentBudget.asStateFlow()

    fun loadBudgetById(budgetId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val budget = getBudgetByIdUseCase(budgetId)
                _currentBudget.value = budget
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load budget: ${e.message}"
                    )
                }
            }
        }
    }

    fun addBudget(
        category: ExpenseCategory,
        amount: Double,
        period: BudgetPeriod,
        startDate: LocalDateTime,
        endDate: LocalDateTime? = null,
        isActive: Boolean = true,
        notificationEnabled: Boolean = false,
        alertThreshold: Double = 0.8
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val budget = Budget(
                    category = category,
                    amount = amount,
                    period = period,
                    startDate = startDate,
                    endDate = endDate,
                    isActive = isActive,
                    notificationEnabled = notificationEnabled,
                    alertThreshold = alertThreshold
                )
                addBudgetUseCase(budget)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Budget created successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to create budget: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateBudget(
        budgetId: Long,
        category: ExpenseCategory,
        amount: Double,
        period: BudgetPeriod,
        startDate: LocalDateTime,
        endDate: LocalDateTime? = null,
        isActive: Boolean = true,
        notificationEnabled: Boolean = false,
        alertThreshold: Double = 0.8
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val budget = Budget(
                    id = budgetId,
                    category = category,
                    amount = amount,
                    period = period,
                    startDate = startDate,
                    endDate = endDate,
                    isActive = isActive,
                    notificationEnabled = notificationEnabled,
                    alertThreshold = alertThreshold
                )
                updateBudgetUseCase(budget)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Budget updated successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to update budget: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteBudget(budgetId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val budget = getBudgetByIdUseCase(budgetId)
                if (budget != null) {
                    deleteBudgetUseCase(budget)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Budget deleted successfully"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to delete budget: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                error = null,
                successMessage = null
            )
        }
    }

    fun clearCurrentBudget() {
        _currentBudget.value = null
    }
}

data class BudgetUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)