package com.example.expensetracker.presentation.screen.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    onBack: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var category by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var amount by remember { mutableStateOf("") }
    var period by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    var isActive by remember { mutableStateOf(true) }
    var notificationEnabled by remember { mutableStateOf(false) }
    var alertThreshold by remember { mutableStateOf("80") }

    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
            onBack()
        }

        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Budget Limit", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Creating budget...", color = MaterialTheme.colorScheme.onBackground)
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
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Selection
        CategorySelectionCard(
            selectedCategory = category,
            onCategorySelected = onCategoryChange
        )

        // Budget Details
        BudgetDetailsCard(
            amount = amount,
            onAmountChange = onAmountChange,
            period = period,
            onPeriodChange = onPeriodChange,
            isActive = isActive,
            onActiveChange = onActiveChange
        )

        // Notifications
        NotificationsCard(
            notificationEnabled = notificationEnabled,
            onNotificationEnabledChange = onNotificationEnabledChange,
            alertThreshold = alertThreshold,
            onAlertThresholdChange = onAlertThresholdChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = amount.isNotBlank() && amount.toDoubleOrNull() != null && amount.toDouble() > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Create Budget",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CategorySelectionCard(
    selectedCategory: ExpenseCategory,
    onCategorySelected: (ExpenseCategory) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ExpenseCategory.entries.toTypedArray()) { cat ->
                    CategoryChip(
                        category = cat,
                        isSelected = selectedCategory == cat,
                        onSelected = { onCategorySelected(cat) }
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
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Limit Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                        onAmountChange(newValue)
                    }
                },
                label = { Text("Budget Limit") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = {
                    Text(
                        text = "$",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            )

            PeriodDropdown(
                selectedPeriod = period,
                onPeriodSelected = onPeriodChange
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Limitation",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Apply budget limits to active tracking",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Threshold Alerts",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Notify when budget usage reaches limits",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                        Text("Receive warning notifications at this percentage")
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
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
                .menuAnchor(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
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
    val tintColor = Color(category.color)
    Card(
        modifier = Modifier
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) tintColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) tintColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        onClick = onSelected
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(category.iconRes, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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