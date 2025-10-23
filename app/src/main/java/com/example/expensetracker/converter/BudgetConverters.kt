package com.example.expensetracker.converter

import androidx.room.TypeConverter
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.ExpenseCategory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BudgetConverters {
    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime {
        return dateString.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }

    @TypeConverter
    fun fromExpenseCategory(category: ExpenseCategory): String {
        return category.name
    }

    @TypeConverter
    fun toExpenseCategory(categoryString: String): ExpenseCategory {
        return ExpenseCategory.valueOf(categoryString)
    }

    @TypeConverter
    fun fromBudgetPeriod(period: BudgetPeriod): String {
        return period.name
    }

    @TypeConverter
    fun toBudgetPeriod(periodString: String): BudgetPeriod {
        return BudgetPeriod.valueOf(periodString)
    }
}