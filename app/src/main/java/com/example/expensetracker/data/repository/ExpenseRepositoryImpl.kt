package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.domain.model.AnalyticsData
import com.example.expensetracker.domain.model.CategoryDistribution
import com.example.expensetracker.domain.model.ComparisonData
import com.example.expensetracker.domain.model.DailySpending
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.domain.model.MonthlyTrend
import com.example.expensetracker.domain.model.RecurrenceType
import com.example.expensetracker.domain.model.SpendingInsights
import com.example.expensetracker.domain.model.WeeklySpending
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.domain.repository.MonthlySummary
import com.example.expensetracker.domain.repository.TimeRange
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import javax.inject.Inject
import kotlin.collections.forEach

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ExpenseRepository {
    override fun getExpenses(): Flow<List<Expense>> {
        return expenseDao.getExpenses().map { entities ->
            entities.map { it.toExpense() }
        }
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)?.toExpense()
    }

    override suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())
    }

    override suspend fun getExpensesByDateRange(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Expense>? {
        return expenseDao.getExpensesByDateRange(
            start.toEpochMillis(),
            end.toEpochMillis()
        ).map { it.toExpense() }
    }

    override suspend fun getExpensesByCategory(category: ExpenseCategory): List<Expense>? {
        return expenseDao.getExpensesByCategory(category.name).map { it.toExpense() }
    }

    override suspend fun getMonthlySummary(
        month: Int,
        year: Int
    ): MonthlySummary {
        val categorySums = expenseDao.getMonthlyCategorySummary(
            month.toString().padStart(2, '0'),
            year.toString()
        )

        val totalAmount = categorySums.sumOf { it.total }

        val categoryBreakdown = categorySums.associate {
            ExpenseCategory.valueOf(it.category) to it.total
        }

        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val averageDailySpending = totalAmount / daysInMonth

        return MonthlySummary(totalAmount, categoryBreakdown, averageDailySpending)
    }

    override suspend fun getAnalyticsData(timeRange: TimeRange): AnalyticsData {
        return withContext(dispatcher) {
            val (startDate, endDate) = getDateRangeForTimeRange(timeRange)

            AnalyticsData(
                monthlyTrends = getMonthlyTrends(6),
                categoryDistribution = getCategoryDistribution(startDate, endDate),
                spendingInsights = getSpendingInsights(timeRange),
                weeklySpending = getWeeklySpending(startDate, 8),
                comparisonData = getComparisonData()
            )
        }
    }

    override suspend fun getCategoryDistribution(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<CategoryDistribution> {
        val expenses = getExpensesByDateRange(startDate, endDate)
        val totalAmount = expenses?.sumOf { it.amount }

        return ExpenseCategory.entries.map { category ->
            val categoryExpenses = expenses?.filter { it.category == category }
            val categoryAmount = categoryExpenses?.sumOf { it.amount }
            val percentage = totalAmount?.let { if (it > 0) (categoryAmount!! / totalAmount) * 100 else 0.0 }

            CategoryDistribution(
                category = category,
                amount = categoryAmount!!,
                percentage = percentage!!,
                count = categoryExpenses.size
            )
        }.filter { it.amount > 0 }
    }

    override suspend fun getMonthlyTrends(months: Int): List<MonthlyTrend> {
        val calendar = Calendar.getInstance()
        val trends = mutableListOf<MonthlyTrend>()

        repeat(months) { i->
            calendar.add(Calendar.MONTH, -i)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)

            val monthStart = LocalDateTime.of(year, month, 1, 0, 0)
            val monthEnd = monthStart.plusMonths(1).minusSeconds(1)

            val monthlyExpenses = getExpensesByDateRange(monthStart, monthEnd)
            val totalAmount = monthlyExpenses?.sumOf { it.amount }

            val dailySpending = monthlyExpenses?.groupBy { it.date.toLocalDate() }
                ?.map { (date, expenses) ->
                    DailySpending(
                        date = date.atStartOfDay(),
                        amount = expenses.sumOf { it.amount },
                        expenses = expenses
                    )
                }

            val highestSpendingDay = dailySpending?.maxByOrNull { it.amount }
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            val averageDailySpending = totalAmount?.div(daysInMonth)

            trends.add(MonthlyTrend(
                month = monthStart.month.name.lowercase().replaceFirstChar { it.uppercase() },
                year = year,
                totalAmount = totalAmount!!,
                averageDailySpending = averageDailySpending!!,
                highestSpendingDay = highestSpendingDay
            ))
        }
        return trends.reversed()
    }

    override suspend fun getSpendingInsights(timeRange: TimeRange): SpendingInsights {
        val (startDate, endDate) = getDateRangeForTimeRange(timeRange)
        val expenses = getExpensesByDateRange(startDate, endDate)

        val totalSpent = expenses?.sumOf { it.amount }
        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate).toDouble()
        val spendingVelocity = totalSpent?.div(daysBetween)

        val biggestExpense = expenses?.maxByOrNull { it.amount }
        val mostFrequentCategory = expenses?.groupBy { it.category }
            ?.maxByOrNull { it.value.size }?.key

        val topExpenses = expenses?.sortedByDescending { it.amount }?.take(3)
        val savingsPotential = topExpenses?.sumOf { it.amount }?.times(0.2)
        val averageMonthlySpending = calculateAverageMonthlySpending(expenses!!)

        return SpendingInsights(
            totalSpent = totalSpent!!,
            averageMonthlySpending = averageMonthlySpending,
            biggestExpense = biggestExpense,
            mostFrequentCategory = mostFrequentCategory,
            savingsPotential = savingsPotential!!,
            spendingVelocity = spendingVelocity!!
        )
    }

    override suspend fun getWeeklySpending(
        startDate: LocalDateTime,
        weeks: Int
    ): List<WeeklySpending> {
        val weeklyData = mutableListOf<WeeklySpending>()

        repeat(weeks) { weekIndex ->
            val weekStart = startDate.minusWeeks(weekIndex.toLong())
            val weekEnd = weekStart.plusDays(6)

            val weeklyExpenses = getExpensesByDateRange(weekStart, weekEnd)
            val totalAmount = weeklyExpenses?.sumOf { it.amount }

            val dailyAverages = mutableMapOf<DayOfWeek, Double>()
            DayOfWeek.entries.forEach { day ->
                val dayExpenses = weeklyExpenses?.filter { it.date.dayOfWeek == day }
                dailyAverages[day] = dayExpenses?.sumOf { it.amount } as Double
            }

            weeklyData.add(WeeklySpending(
                weekStart = weekStart,
                weekEnd = weekEnd,
                totalAmount = totalAmount!!,
                dailyAverages = dailyAverages
            ))
        }

        return weeklyData.reversed()
    }

    override suspend fun getComparisonData(): ComparisonData {
        val now = LocalDateTime.now()
        val currentMonth = getMonthlySummary(now.monthValue, now.year)
        val previousMonthDate = now.minusMonths(1)
        val previousMonth = getMonthlySummary(previousMonthDate.monthValue, previousMonthDate.year)

        val percentageChange = if (previousMonth.totalAmount > 0) {
            ((currentMonth.totalAmount - previousMonth.totalAmount) / previousMonth.totalAmount) * 100
        } else 0.0

        val categoryChanges = mutableMapOf<ExpenseCategory, Double>()
        ExpenseCategory.entries.forEach { category ->
            val current = currentMonth.categoryBreakdown[category] ?: 0.0
            val previous = previousMonth.categoryBreakdown[category] ?: 0.0
            val change = if (previous > 0) ((current - previous) / previous) * 100 else 0.0
            categoryChanges[category] = change
        }

        return ComparisonData(
            currentMonth = currentMonth,
            previousMonth = previousMonth,
            percentageChange = percentageChange,
            categoryChanges = categoryChanges
        )
    }

    private fun getDateRangeForTimeRange(timeRange: TimeRange): Pair<LocalDateTime, LocalDateTime> {
        val endDate = LocalDateTime.now()
        return when (timeRange) {
            TimeRange.LAST_30_DAYS -> endDate.minusDays(30) to endDate
            TimeRange.LAST_3_MONTHS -> endDate.minusMonths(3) to endDate
            TimeRange.LAST_6_MONTHS -> endDate.minusMonths(6) to endDate
            TimeRange.LAST_YEAR -> endDate.minusYears(1) to endDate
            TimeRange.CUSTOM -> endDate.minusMonths(1) to endDate
        }
    }

    private fun calculateAverageMonthlySpending(expenses: List<Expense>): Double {
        if (expenses.isEmpty()) return 0.0

        val monthlyGroups = expenses.groupBy {
            YearMonth.from(it.date)
        }
        return monthlyGroups.values.map { monthExpenses ->
            monthExpenses.sumOf { it.amount }
        }.average()
    }
}

private fun ExpenseEntity.toExpense(): Expense {
    return Expense(
        id = id,
        title = title,
        amount = amount,
        category = ExpenseCategory.valueOf(category),
        date = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()),
        notes = notes,
        isRecurring = isRecurring,
        recurrenceType = recurrenceType?.let { RecurrenceType.valueOf(it) }
    )
}

private fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        title = title,
        amount = amount,
        category = category.name,
        date = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        notes = notes,
        isRecurring = isRecurring,
        recurrenceType = recurrenceType?.name
    )
}

private fun LocalDateTime.toEpochMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}












