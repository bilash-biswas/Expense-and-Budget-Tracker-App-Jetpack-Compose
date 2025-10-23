package com.example.expensetracker.presentation.screen.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    onBack: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Form state
    var category by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var amount by remember { mutableStateOf("") }
    var period by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    var isActive by remember { mutableStateOf(true) }
    var notificationEnabled by remember { mutableStateOf(false) }
    var alertThreshold by remember { mutableStateOf("80") }

    // Handle success/error messages
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }

        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Budget") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Creating budget...")
            }
        } else {
            AddBudgetForm(
                category = category,
                onCategoryChange = { category = it },
                amount = amount,
                onAmountChange = { amount = it },
                period = period,
                onPeriodChange = { period = it },
                isActive = isActive,
                onActiveChange = { isActive = it },
                notificationEnabled = notificationEnabled,
                onNotificationEnabledChange = { notificationEnabled = it },
                alertThreshold = alertThreshold,
                onAlertThresholdChange = { alertThreshold = it },
                onSubmit = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    val thresholdValue = alertThreshold.toDoubleOrNull() ?: 80.0

                    if (amountValue > 0) {
                        viewModel.addBudget(
                            category = category,
                            amount = amountValue,
                            period = period,
                            startDate = LocalDateTime.now(),
                            endDate = calculateEndDate(period),
                            isActive = isActive,
                            notificationEnabled = notificationEnabled,
                            alertThreshold = thresholdValue / 100.0
                        )
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please enter a valid amount")
                        }
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun AddBudgetForm(
    category: ExpenseCategory,
    onCategoryChange: (ExpenseCategory) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    period: BudgetPeriod,
    onPeriodChange: (BudgetPeriod) -> Unit,
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit,
    notificationEnabled: Boolean,
    onNotificationEnabledChange: (Boolean) -> Unit,
    alertThreshold: String,
    onAlertThresholdChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Category Selection
        CategorySelectionCard(
            selectedCategory = category,
            onCategorySelected = onCategoryChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Budget Details
        BudgetDetailsCard(
            amount = amount,
            onAmountChange = onAmountChange,
            period = period,
            onPeriodChange = onPeriodChange,
            isActive = isActive,
            onActiveChange = onActiveChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications
        NotificationsCard(
            notificationEnabled = notificationEnabled,
            onNotificationEnabledChange = onNotificationEnabledChange,
            alertThreshold = alertThreshold,
            onAlertThresholdChange = onAlertThresholdChange
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = amount.isNotBlank() && amount.toDoubleOrNull() != null && amount.toDouble() > 0
        ) {
            Text("Create Budget")
        }
    }
}

@Composable
fun CategorySelectionCard(
    selectedCategory: ExpenseCategory,
    onCategorySelected: (ExpenseCategory) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(180.dp)
            ) {
                items(ExpenseCategory.entries.toTypedArray()) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onSelected = { onCategorySelected(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetDetailsCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    period: BudgetPeriod,
    onPeriodChange: (BudgetPeriod) -> Unit,
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Budget Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                        onAmountChange(newValue)
                    }
                },
                label = { Text("Budget Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("$") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Period
            PeriodDropdown(
                selectedPeriod = period,
                onPeriodSelected = onPeriodChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Active Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Active Budget")
                Switch(
                    checked = isActive,
                    onCheckedChange = onActiveChange
                )
            }
        }
    }
}

@Composable
fun NotificationsCard(
    notificationEnabled: Boolean,
    onNotificationEnabledChange: (Boolean) -> Unit,
    alertThreshold: String,
    onAlertThresholdChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Notifications")
                Switch(
                    checked = notificationEnabled,
                    onCheckedChange = onNotificationEnabledChange
                )
            }

            if (notificationEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = alertThreshold,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d{1,3}$"))) {
                            onAlertThresholdChange(newValue)
                        }
                    },
                    label = { Text("Alert Threshold (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("%") },
                    singleLine = true,
                    supportingText = {
                        Text("Receive alerts when budget usage reaches this percentage")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDropdown(
    selectedPeriod: BudgetPeriod,
    onPeriodSelected: (BudgetPeriod) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedPeriod.name.lowercase().replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text("Budget Period") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            BudgetPeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = {
                        Text(
                            period.name.lowercase().replaceFirstChar { it.uppercase() } +
                                    " (${getPeriodDescription(period)})"
                        )
                    },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(
    category: ExpenseCategory,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val containerColor = if (isSelected) {
        Color(category.color)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .padding(2.dp),
        onClick = onSelected,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = getCategoryIcon(category),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                color = contentColor
            )
        }
    }
}

// Helper functions
private fun getCategoryIcon(category: ExpenseCategory): String {
    return when (category) {
        ExpenseCategory.FOOD -> "🍔"
        ExpenseCategory.TRANSPORT -> "🚗"
        ExpenseCategory.ENTERTAINMENT -> "🎬"
        ExpenseCategory.SHOPPING -> "🛍️"
        ExpenseCategory.HEALTH -> "🏥"
        ExpenseCategory.BILLS -> "💡"
        ExpenseCategory.EDUCATION -> "📚"
        ExpenseCategory.OTHER -> "✈️"
        else -> "💰"
    }
}

private fun getPeriodDescription(period: BudgetPeriod): String {
    return when (period) {
        BudgetPeriod.WEEKLY -> "7 days"
        BudgetPeriod.MONTHLY -> "30 days"
        BudgetPeriod.QUARTERLY -> "3 months"
        BudgetPeriod.YEARLY -> "12 months"
    }
}

private fun calculateEndDate(period: BudgetPeriod): LocalDateTime {
    val now = LocalDateTime.now()
    return when (period) {
        BudgetPeriod.WEEKLY -> now.plusWeeks(1)
        BudgetPeriod.MONTHLY -> now.plusMonths(1)
        BudgetPeriod.QUARTERLY -> now.plusMonths(3)
        BudgetPeriod.YEARLY -> now.plusYears(1)
    }
}