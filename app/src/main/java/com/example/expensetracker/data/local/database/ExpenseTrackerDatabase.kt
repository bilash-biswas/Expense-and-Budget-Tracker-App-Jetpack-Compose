package com.example.expensetracker.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.expensetracker.data.local.dao.BudgetDao
import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.entity.BudgetEntity
import com.example.expensetracker.data.local.entity.ExpenseEntity

@Database(
    entities = [
        ExpenseEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseTrackerDatabase? = null

        fun getInstance(context: Context): ExpenseTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseTrackerDatabase::class.java,
                    "expense_tracker_db"
                )
                    .fallbackToDestructiveMigration() // This will recreate DB if schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}