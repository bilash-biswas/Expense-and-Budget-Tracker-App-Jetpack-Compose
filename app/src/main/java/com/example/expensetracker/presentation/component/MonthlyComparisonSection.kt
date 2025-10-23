package com.example.expensetracker.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.domain.model.ComparisonData
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.util.format

@Composable
fun MonthlyComparisonSection(comparisonData: ComparisonData) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Monthly Comparison",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ComparisonItem(
                    title = "This Month",
                    amount = comparisonData.currentMonth.totalAmount,
                    subtitle = "Current"
                )

                ComparisonItem(
                    title = "Last Month",
                    amount = comparisonData.previousMonth.totalAmount,
                    subtitle = "Previous"
                )

                ComparisonItem(
                    title = "Change",
                    amount = comparisonData.percentageChange,
                    subtitle = "Percentage",
                    isPercentage = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Category Changes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(comparisonData.categoryChanges.entries.toList()) { (category, change) ->
                    CategoryChangeChip(category = category, change = change)
                }
            }
        }
    }
}

@Composable
fun ComparisonItem(
    title: String,
    amount: Double,
    subtitle: String,
    isPercentage: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isPercentage) {
                "${if (amount > 0) "+" else ""}${amount.format(1)}%"
            } else {
                "$${amount.format(2)}"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = when {
                isPercentage && amount > 0 -> MaterialTheme.colorScheme.error
                isPercentage && amount < 0 -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CategoryChangeChip(category: ExpenseCategory, change: Double) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(category.color).copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.iconRes,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "${if (change > 0) "+" else ""}${change.format(1)}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (change > 0) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}