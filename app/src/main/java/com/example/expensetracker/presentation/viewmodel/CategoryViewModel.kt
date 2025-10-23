package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor() : ViewModel() {

    private val _categoriesState = MutableStateFlow(CategoriesState())
    val categoriesState: StateFlow<CategoriesState> = _categoriesState.asStateFlow()

    fun updateCategoryColor(category: ExpenseCategory, newColor: Long) {
        viewModelScope.launch {
            // In a real app, you'd update this in the database
            // For now, we'll just update the state
            _categoriesState.update { state ->
                state.copy(
                    updatedCategories = state.updatedCategories + (category to newColor)
                )
            }
        }
    }

    fun updateCategoryIcon(category: ExpenseCategory, newIcon: String) {
        viewModelScope.launch {
            // Update category icon logic
        }
    }

    fun resetCategoryChanges() {
        _categoriesState.update { CategoriesState() }
    }
}

data class CategoriesState(
    val updatedCategories: Map<ExpenseCategory, Long> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)