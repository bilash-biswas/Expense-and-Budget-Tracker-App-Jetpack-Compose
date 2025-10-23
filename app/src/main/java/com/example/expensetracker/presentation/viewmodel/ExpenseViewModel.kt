package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.MonthlySummary
import com.example.expensetracker.domain.usecase.expense.AddExpenseUseCase
import com.example.expensetracker.domain.usecase.expense.DeleteExpenseUseCase
import com.example.expensetracker.domain.usecase.expense.GetExpenseByIdUseCase
import com.example.expensetracker.domain.usecase.expense.GetExpensesByCategoryUseCase
import com.example.expensetracker.domain.usecase.expense.GetExpensesByDateRangeUseCase
import com.example.expensetracker.domain.usecase.expense.GetExpensesUseCase
import com.example.expensetracker.domain.usecase.expense.GetMonthlySummaryUseCase
import com.example.expensetracker.domain.usecase.expense.UpdateExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val getExpensesByCategoryUseCase: GetExpensesByCategoryUseCase,
    private val getExpensesByDateRangeUseCase: GetExpensesByDateRangeUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase
) : ViewModel() {
    private val _expenses = getExpensesUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val expenses: StateFlow<List<Expense>> = _expenses

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            addExpenseUseCase(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            updateExpenseUseCase(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            deleteExpenseUseCase(expense)
        }
    }

    fun getExpenseById(id: Long): Expense? {
        return _expenses.value.find { it.id == id }
    }

    fun updateSelectedMonth(month: Int, year: Int) {
        viewModelScope.launch {
            val summary = getMonthlySummaryUseCase(month, year)
            _uiState.update {
                it.copy(monthlySummary = summary)
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}

data class ExpenseUiState(
    val monthlySummary: MonthlySummary? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)