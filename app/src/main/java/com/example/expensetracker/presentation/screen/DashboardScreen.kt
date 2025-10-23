// presentation/screen/DashboardScreen.kt
package com.example.expensetracker.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.presentation.navigation.BottomNavItem
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel
import com.example.expensetracker.presentation.viewmodel.ExpenseViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    navController: NavController
) {
    val budgets by budgetViewModel.budgets.collectAsState()
    val expenses by expenseViewModel.expenses.collectAsState()
    val monthlySummary by expenseViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // Load current month summary
        val now = LocalDateTime.now()
        expenseViewModel.updateSelectedMonth(now.monthValue, now.year)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome Header
        WelcomeHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Stats
        QuickStatsSection(
            totalExpenses = expenses.size,
            totalBudgets = budgets.size,
            monthlySpending = monthlySummary.monthlySummary?.totalAmount ?: 0.0,
            activeBudgets = budgets.count { it.isActive }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Expenses
        RecentExpensesSection(
            expenses = expenses.take(5),
            onViewAllExpenses = { navController.navigate("expenses") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Budget Overview
        BudgetOverviewSection(
            budgets = budgets,
            onViewAllBudgets = { navController.navigate(BottomNavItem.Budget.route) },
            expenses = expenses
        )
    }
}

@Composable
fun WelcomeHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Here's your financial overview",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickStatsSection(
    totalExpenses: Int,
    totalBudgets: Int,
    monthlySpending: Double,
    activeBudgets: Int
) {
    Column {
        Text(
            text = "Quick Stats",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                StatCard(
                    title = "Monthly Spending",
                    value = "$${"%.2f".format(monthlySpending)}",
                    icon = Icons.Default.AttachMoney,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item {
                StatCard(
                    title = "Total Expenses",
                    value = totalExpenses.toString(),
                    icon = Icons.Default.TrendingUp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            item {
                StatCard(
                    title = "Active Budgets",
                    value = "$activeBudgets/$totalBudgets",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier
            .width(140.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    tint = color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RecentExpensesSection(
    expenses: List<Expense>,
    onViewAllExpenses: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onViewAllExpenses() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent expenses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    expenses.forEach { expense ->
                        RecentExpenseItem(expense = expense)
                    }
                }
            }
        }
    }
}

@Composable
fun RecentExpenseItem(expense: Expense) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = expense.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = expense.category.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "-$${"%.2f".format(expense.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = expense.date.format(DateTimeFormatter.ofPattern("MMM dd")),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BudgetOverviewSection(
    budgets: List<Budget>,
    onViewAllBudgets: () -> Unit,
    expenses: List<Expense>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onViewAllBudgets() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (budgets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No budgets set up",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    budgets.take(3).forEach { budget ->
                        BudgetOverviewItem(budget = budget, expenses = expenses)
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetOverviewItem(
    budget: Budget,
    expenses: List<Expense> = emptyList()
) {
    // Calculate actual progress for dashboard
    val (spentAmount, progress) = calculateBudgetProgress(budget, expenses)
    val isOverBudget = spentAmount > budget.amount

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(MaterialTheme.shapes.small)
                .background(androidx.compose.ui.graphics.Color(budget.category.color)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = budget.category.iconRes,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Budget Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = budget.category.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$${"%.2f".format(budget.amount)} • ${budget.period.name.lowercase()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Progress Percentage
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = when {
                isOverBudget -> MaterialTheme.colorScheme.error
                progress >= budget.alertThreshold -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

private fun calculateBudgetProgress(
    budget: Budget,
    expenses: List<Expense>
): Pair<Double, Float> {
    val periodExpenses = expenses.filter { expense ->
        expense.category == budget.category && isExpenseInBudgetPeriod(expense, budget)
    }

    // Calculate total spent amount
    val spentAmount = periodExpenses.sumOf { it.amount }

    // Calculate progress percentage (capped for display)
    val progress = (spentAmount / budget.amount).toFloat()

    return Pair(spentAmount, progress)
}

private fun isExpenseInBudgetPeriod(
    expense: Expense,
    budget: Budget
): Boolean {
    return expense.date.isAfter(budget.startDate) &&
            expense.date.isBefore(budget.endDate)
}

