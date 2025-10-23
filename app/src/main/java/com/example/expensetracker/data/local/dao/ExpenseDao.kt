package com.example.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    suspend fun getExpensesByDateRange(start: Long, end: Long): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    suspend fun getExpensesByCategory(category: String): List<ExpenseEntity>

    @Query("SELECT SUM(amount) as total, category FROM expenses WHERE strftime('%m', date/1000, 'unixepoch') = :month AND strftime('%Y', date/1000, 'unixepoch') = :year GROUP BY category")
    suspend fun getMonthlyCategorySummary(month: String, year: String): List<CategorySum>
}

data class CategorySum(
    val category: String,
    val total: Double
)