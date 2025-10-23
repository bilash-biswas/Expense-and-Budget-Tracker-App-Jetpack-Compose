package com.example.expensetracker.domain.usecase.analytics

import com.example.expensetracker.domain.model.CategoryDistribution
import com.example.expensetracker.domain.repository.ExpenseRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetCategoryDistributionUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(startDate: LocalDateTime, endDate: LocalDateTime): List<CategoryDistribution> {
        return repository.getCategoryDistribution(startDate, endDate)
    }
}