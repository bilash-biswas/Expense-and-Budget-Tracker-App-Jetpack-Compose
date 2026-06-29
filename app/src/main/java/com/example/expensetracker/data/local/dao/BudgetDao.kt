package com.example.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY startDate DESC")
    fun getBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: Long): BudgetEntity?

    @Insert
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE isActive = 1")
    suspend fun getActiveBudgets(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE category = :category AND isActive = 1")
    suspend fun getActiveBudgetByCategory(category: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE isActive = 1 AND startDate <= :currentDate AND (endDate IS NULL OR endDate >= :currentDate)")
    suspend fun getCurrentActiveBudgets(currentDate: String): List<BudgetEntity>

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: Long)

    @Query("UPDATE budgets SET isActive = :isActive WHERE id = :id")
    suspend fun updateBudgetStatus(id: Long, isActive: Boolean)

    @Query("SELECT * FROM budgets WHERE category = :category AND period = :period AND isActive = 1")
    suspend fun getBudgetByCategoryAndPeriod(category: String, period: String): BudgetEntity?

    @Query("""
        SELECT b.*, 
               COALESCE((
                   SELECT SUM(e.amount) 
                   FROM expenses e 
                   WHERE e.category = b.category 
                   AND e.date BETWEEN 
                       CAST(strftime('%s', CASE b.period 
                           WHEN 'WEEKLY' THEN datetime('now', 'weekday 0', '-6 days')
                           WHEN 'MONTHLY' THEN datetime('now', 'start of month')
                           WHEN 'QUARTERLY' THEN datetime('now', 'start of month', '-' || ((strftime('%m', 'now') - 1) % 3) || ' months')
                           WHEN 'YEARLY' THEN datetime('now', 'start of year')
                       END) AS INTEGER) * 1000
                   AND CAST(strftime('%s', 'now') AS INTEGER) * 1000
               ), 0) as currentSpending
        FROM budgets b
        WHERE b.isActive = 1
        AND b.notificationEnabled = 1
    """)
    suspend fun getBudgetsWithCurrentSpending(): List<BudgetWithSpending>

    // For analytics - get budget utilization by category
    @Query("""
        SELECT b.category, 
               b.amount as budgetAmount,
               COALESCE((
                   SELECT SUM(e.amount) 
                   FROM expenses e 
                   WHERE e.category = b.category 
                   AND e.date BETWEEN 
                       CAST(strftime('%s', CASE b.period 
                           WHEN 'WEEKLY' THEN datetime('now', 'weekday 0', '-6 days')
                           WHEN 'MONTHLY' THEN datetime('now', 'start of month')
                           WHEN 'QUARTERLY' THEN datetime('now', 'start of month', '-' || ((strftime('%m', 'now') - 1) % 3) || ' months')
                           WHEN 'YEARLY' THEN datetime('now', 'start of year')
                       END) AS INTEGER) * 1000
                   AND CAST(strftime('%s', 'now') AS INTEGER) * 1000
               ), 0) as spentAmount
        FROM budgets b
        WHERE b.isActive = 1
    """)
    suspend fun getBudgetUtilization(): List<BudgetUtilization>
}

data class BudgetWithSpending(
    val id: Long,
    val category: String,
    val amount: Double,
    val period: String,
    val startDate: String,
    val endDate: String?,
    val isActive: Boolean,
    val notificationEnabled: Boolean,
    val alertThreshold: Double,
    val currentSpending: Double
)

data class BudgetUtilization(
    val category: String,
    val budgetAmount: Double,
    val spentAmount: Double
) {
    val utilizationRate: Double
        get() = if (budgetAmount > 0) spentAmount / budgetAmount else 0.0

    val remainingAmount: Double
        get() = budgetAmount - spentAmount
}