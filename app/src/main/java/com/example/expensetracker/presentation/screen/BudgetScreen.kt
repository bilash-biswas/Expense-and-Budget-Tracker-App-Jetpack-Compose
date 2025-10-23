// presentation/screen/BudgetScreen.kt
package com.example.expensetracker.presentation.screen

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel
import com.example.expensetracker.presentation.viewmodel.ExpenseViewModel
import java.time.format.DateTimeFormatter

@Composable
fun BudgetScreen(
    onNavigateToAddBudget: () -> Unit,
    onNavigateToEditBudget: (Long) -> Unit,
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val budgets by budgetViewModel.budgets.collectAsState()
    val uiState by budgetViewModel.uiState.collectAsState()

    val expenses by expenseViewModel.expenses.collectAsState()
    val monthlyExpense by expenseViewModel.uiState.collectAsState()

    Log.d("BudgetScreen", "Recomposing with budgets: $budgets")
    Log.d("BudgetScreen", "Recomposing with expense: $expenses")
    Log.d("BudgetScreen", "Recomposing with budgets: $uiState")
    Log.d("BudgetScreen", "Recomposing with expense: $monthlyExpense")

    LaunchedEffect(Unit) {
        // Load any initial data if needed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BudgetHeaderSection(
            totalBudgets = budgets.size,
            activeBudgets = budgets.count { it.isActive }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Budgets List
        if (budgets.isEmpty()) {
            EmptyBudgetsState()
        } else {
            BudgetsList(
                budgets = budgets,
                onBudgetClick = { budgetId ->
                    onNavigateToEditBudget(budgetId)
                },
                addBudget = onNavigateToAddBudget,
                expenses = expenses
            )
        }
    }

    // Loading State
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun BudgetHeaderSection(
    totalBudgets: Int,
    activeBudgets: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Budget Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetStatItem(
                    title = "Total Budgets",
                    value = totalBudgets.toString(),
                    icon = Icons.Default.AttachMoney
                )

                BudgetStatItem(
                    title = "Active",
                    value = activeBudgets.toString(),
                    icon = Icons.Default.Notifications
                )
            }
        }
    }
}

@Composable
fun BudgetStatItem(
    title: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BudgetsList(
    budgets: List<Budget>,
    onBudgetClick: (Long) -> Unit,
    addBudget: () -> Unit,
    expenses: List<Expense>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Budgets",
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = addBudget,
            ) {
                Text(text = "+ Add Budget")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(budgets, key = { it.id }) { budget ->
                BudgetItem(
                    budget = budget,
                    onClick = { onBudgetClick(budget.id) },
                    expenses = expenses
                )
            }
        }
    }
}

@Composable
fun BudgetItem(
    budget: Budget,
    expenses: List<Expense>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category and Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(budget.category.color)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = budget.category.iconRes,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = budget.category.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = budget.period.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge
                BudgetStatusBadge(isActive = budget.isActive)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount and Progress - NOW WITH REAL DATA!
            BudgetProgressSection(
                budget = budget,
                expenses = expenses
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dates and Notifications
            BudgetMetaInfo(budget = budget)
        }
    }
}

@Composable
fun BudgetStatusBadge(isActive: Boolean) {
    val backgroundColor = if (isActive) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isActive) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (isActive) "Active" else "Inactive",
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BudgetProgressSection(
    budget: Budget,
    expenses: List<Expense> = emptyList()
) {
    // Calculate actual spending for this budget's category and period
    val (spentAmount, progress) = calculateBudgetProgress(budget, expenses)
    val remainingAmount = budget.amount - spentAmount
    val isOverBudget = spentAmount > budget.amount

    val progressColor = when {
        isOverBudget -> MaterialTheme.colorScheme.error
        progress >= budget.alertThreshold -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }

    Column {
        // Amount Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Budget: $${"%.2f".format(budget.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Spent: $${"%.2f".format(spentAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isOverBudget) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = progress.coerceAtMost(1f), // Cap at 100% for display
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Progress Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Remaining: $${"%.2f".format(remainingAmount.coerceAtLeast(0.0))}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isOverBudget) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = if (isOverBudget) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        // Alert message if over budget or near threshold
        if (isOverBudget) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⚠️ Over budget by $${"%.2f".format(-remainingAmount)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        } else if (progress >= budget.alertThreshold) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "🔔 Approaching budget limit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Helper function to calculate budget progress
private fun calculateBudgetProgress(
    budget: Budget,
    expenses: List<Expense>
): Pair<Double, Float> {
    // Filter expenses for this budget's category and period

    Log.e("expense", expenses.toString())
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
@Composable
fun BudgetMetaInfo(budget: Budget) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Date Info
        Column {
            Text(
                text = "From: ${budget.startDate.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            budget.endDate?.let { endDate ->
                Text(
                    text = "To: ${endDate.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Notification Indicator
        if (budget.notificationEnabled) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications enabled",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EmptyBudgetsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = "No budgets",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Budgets Yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your first budget to start tracking your spending",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}