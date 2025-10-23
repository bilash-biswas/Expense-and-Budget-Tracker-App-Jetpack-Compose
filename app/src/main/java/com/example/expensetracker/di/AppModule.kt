package com.example.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.example.expensetracker.data.local.dao.BudgetDao
import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.database.ExpenseTrackerDatabase
import com.example.expensetracker.data.repository.BudgetRepositoryImpl
import com.example.expensetracker.data.repository.ExpenseRepositoryImpl
import com.example.expensetracker.domain.repository.BudgetRepository
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.domain.service.ExportService
import com.example.expensetracker.domain.usecase.ExportBudgetsToCsvUseCase
import com.example.expensetracker.domain.usecase.ExportExpensesToCsvUseCase
import com.example.expensetracker.domain.usecase.GeneratePdfReportUseCase
import com.example.expensetracker.domain.usecase.budget.CreateBudgetUseCase
import com.example.expensetracker.domain.usecase.budget.DeleteBudgetUseCase
import com.example.expensetracker.domain.usecase.budget.GetBudgetByIdUseCase
import com.example.expensetracker.domain.usecase.budget.GetBudgetsUseCase
import com.example.expensetracker.domain.usecase.budget.UpdateBudgetUseCase
import com.example.expensetracker.domain.usecase.expense.AddExpenseUseCase
import com.example.expensetracker.domain.usecase.expense.DeleteExpenseUseCase
import com.example.expensetracker.domain.usecase.expense.GetExpenseByIdUseCase
import com.example.expensetracker.domain.usecase.expense.GetExpensesByCategoryUseCase
import com.example.expensetracker.domain.usecase.expense.GetExpensesByDateRangeUseCase
import com.example.expensetracker.domain.usecase.expense.GetExpensesUseCase
import com.example.expensetracker.domain.usecase.expense.GetMonthlySummaryUseCase
import com.example.expensetracker.domain.usecase.expense.UpdateExpenseUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideExpenseTrackerDatabase(@ApplicationContext context: Context): ExpenseTrackerDatabase {
        return ExpenseTrackerDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: ExpenseTrackerDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun provideBudgetDao(database: ExpenseTrackerDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(expenseDao: ExpenseDao): ExpenseRepository {
        return ExpenseRepositoryImpl(expenseDao)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(budgetDao: BudgetDao): BudgetRepository {
        return BudgetRepositoryImpl(budgetDao)
    }

    @Provides
    @Singleton
    fun provideGetExpensesUseCase(repository: ExpenseRepository): GetExpensesUseCase {
        return GetExpensesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddExpenseUseCase(repository: ExpenseRepository): AddExpenseUseCase {
        return AddExpenseUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateExpenseUseCase(repository: ExpenseRepository): UpdateExpenseUseCase {
        return UpdateExpenseUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteExpenseUseCase(repository: ExpenseRepository): DeleteExpenseUseCase {
        return DeleteExpenseUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMonthlySummaryUseCase(repository: ExpenseRepository): GetMonthlySummaryUseCase {
        return GetMonthlySummaryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetExpenseByIdUseCase(repository: ExpenseRepository): GetExpenseByIdUseCase {
        return GetExpenseByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetExpenseByCategoryUseCase(repository: ExpenseRepository): GetExpensesByCategoryUseCase {
        return GetExpensesByCategoryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetExpenseByDateUseCase(repository: ExpenseRepository): GetExpensesByDateRangeUseCase {
        return GetExpensesByDateRangeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBudgetsUseCase(repository: BudgetRepository): GetBudgetsUseCase {
        return GetBudgetsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBudgetByIdUseCase(repository: BudgetRepository): GetBudgetByIdUseCase {
        return GetBudgetByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddBudgetUseCase(repository: BudgetRepository): CreateBudgetUseCase {
        return CreateBudgetUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateBudgetUseCase(repository: BudgetRepository): UpdateBudgetUseCase {
        return UpdateBudgetUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteBudgetUseCase(repository: BudgetRepository): DeleteBudgetUseCase {
        return DeleteBudgetUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideExportService(
        expenseRepository: ExpenseRepository,
        budgetRepository: BudgetRepository
    ): ExportService {
        return ExportService(expenseRepository, budgetRepository)
    }

    @Provides
    @Singleton
    fun provideExportExpensesToCsvUseCase(exportService: ExportService): ExportExpensesToCsvUseCase {
        return ExportExpensesToCsvUseCase(exportService)
    }

    @Provides
    @Singleton
    fun provideExportBudgetsToCsvUseCase(exportService: ExportService): ExportBudgetsToCsvUseCase {
        return ExportBudgetsToCsvUseCase(exportService)
    }

    @Provides
    @Singleton
    fun provideGeneratePdfReportUseCase(exportService: ExportService): GeneratePdfReportUseCase {
        return GeneratePdfReportUseCase(exportService)
    }


}