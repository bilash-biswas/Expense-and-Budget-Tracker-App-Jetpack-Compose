package com.example.expensetracker.presentation.screen.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(
    budgetId: Long,
    onBack: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentBudget by viewModel.currentBudget.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load budget when screen opens
    LaunchedEffect(budgetId) {
        viewModel.loadBudgetById(budgetId)
    }

    // Handle success/error messages
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
            if (message.contains("deleted")) {
                onBack()
            }
        }

        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Budget") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = currentBudget != null
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Budget")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading && currentBudget == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading budget...")
            }
        } else if (currentBudget == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Budget not found")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Go Back")
                }
            }
        } else {
            currentBudget?.let { budget ->
                EditBudgetForm(
                    budget = budget,
                    onUpdate = { updatedBudget ->
                        viewModel.updateBudget(
                            budgetId = updatedBudget.id,
                            category = updatedBudget.category,
                            amount = updatedBudget.amount,
                            period = updatedBudget.period,
                            startDate = updatedBudget.startDate,
                            endDate = updatedBudget.endDate,
                            isActive = updatedBudget.isActive,
                            notificationEnabled = updatedBudget.notificationEnabled,
                            alertThreshold = updatedBudget.alertThreshold
                        )
                    },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Budget") },
                text = { Text("Are you sure you want to delete this budget? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteBudget(budgetId)
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun EditBudgetForm(
    budget: com.example.expensetracker.domain.model.Budget,
    onUpdate: (com.example.expensetracker.domain.model.Budget) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    // Form state initialized with current budget values
    var category by remember { mutableStateOf(budget.category) }
    var amount by remember { mutableStateOf(budget.amount.toString()) }
    var period by remember { mutableStateOf(budget.period) }
    var isActive by remember { mutableStateOf(budget.isActive) }
    var notificationEnabled by remember { mutableStateOf(budget.notificationEnabled) }
    var alertThreshold by remember { mutableStateOf((budget.alertThreshold * 100).toInt().toString()) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Reuse the same form components from AddBudgetScreen
        CategorySelectionCard(
            selectedCategory = category,
            onCategorySelected = { category = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        BudgetDetailsCard(
            amount = amount,
            onAmountChange = { amount = it },
            period = period,
            onPeriodChange = { period = it },
            isActive = isActive,
            onActiveChange = { isActive = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        NotificationsCard(
            notificationEnabled = notificationEnabled,
            onNotificationEnabledChange = { notificationEnabled = it },
            alertThreshold = alertThreshold,
            onAlertThresholdChange = { alertThreshold = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { /* Navigate back */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    val thresholdValue = alertThreshold.toDoubleOrNull() ?: 80.0

                    if (amountValue > 0) {
                        val updatedBudget = budget.copy(
                            category = category,
                            amount = amountValue,
                            period = period,
                            isActive = isActive,
                            notificationEnabled = notificationEnabled,
                            alertThreshold = thresholdValue / 100.0
                        )
                        onUpdate(updatedBudget)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading && amount.isNotBlank() && amount.toDoubleOrNull() != null && amount.toDouble() > 0
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Update Budget")
                }
            }
        }
    }
}