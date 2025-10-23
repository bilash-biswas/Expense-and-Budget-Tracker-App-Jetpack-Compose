package com.example.expensetracker.presentation.screen.expense

import android.util.Log
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.ExpenseCategory
import com.example.expensetracker.domain.model.RecurrenceType
import com.example.expensetracker.presentation.component.CustomDatePickerDialog
import com.example.expensetracker.presentation.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    expenseId: Long,
    onBack: () -> Unit, // Changed from NavController to callback
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Find the expense from the list
    val expense = viewModel.getExpenseById(expenseId)
    Log.d("EditExpenseScreen", "Expense: $expense")

    // State for form fields
    var title by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(ExpenseCategory.FOOD) }
    var notes by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(LocalDateTime.now()) }
    var isRecurring by rememberSaveable { mutableStateOf(false) }
    var recurrenceType by rememberSaveable { mutableStateOf(RecurrenceType.MONTHLY) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Show error message if expense not found
    LaunchedEffect(expense) {
        if (expense != null) {
            title = expense.title
            amount = expense.amount.toString()
            selectedCategory = expense.category
            notes = expense.notes
            date = expense.date
            isRecurring = expense.isRecurring
            recurrenceType = expense.recurrenceType ?: RecurrenceType.MONTHLY
        }
    }


    if (expense == null && expenseId != 0L) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading expense...")
            }
        }
    } else if (expense != null) {
        EditExpenseContent(
            expense = expense,
            title = title,
            onTitleChange = { title = it },
            amount = amount,
            onAmountChange = { amount = it },
            selectedCategory = selectedCategory,
            onCategoryChange = { selectedCategory = it },
            notes = notes,
            onNotesChange = { notes = it },
            date = date,
            onDateChange = { date = it },
            isRecurring = isRecurring,
            onRecurringChange = { isRecurring = it },
            recurrenceType = recurrenceType,
            onRecurrenceTypeChange = { recurrenceType = it },
            onUpdateExpense = { updatedExpense ->
                coroutineScope.launch {
                    viewModel.updateExpense(updatedExpense)
                    snackbarHostState.showSnackbar("Expense updated successfully")
                    onBack() // Use callback instead of navController.popBackStack()
                }
            },
            onDeleteExpense = { expenseToDelete ->
                coroutineScope.launch {
                    viewModel.deleteExpense(expenseToDelete)
                    snackbarHostState.showSnackbar("Expense deleted successfully")
                    onBack() // Use callback instead of navController.popBackStack()
                }
            },
            showDeleteDialog = showDeleteDialog,
            onShowDeleteDialogChange = { showDeleteDialog = it },
            showDatePicker = showDatePicker,
            onShowDatePickerChange = { showDatePicker = it },
            modifier = Modifier.padding(4.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Expense not found")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseContent(
    expense: Expense,
    title: String,
    onTitleChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedCategory: ExpenseCategory,
    onCategoryChange: (ExpenseCategory) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    date: LocalDateTime,
    onDateChange: (LocalDateTime) -> Unit,
    isRecurring: Boolean,
    onRecurringChange: (Boolean) -> Unit,
    recurrenceType: RecurrenceType,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onUpdateExpense: (Expense) -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    showDeleteDialog: Boolean,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    showDatePicker: Boolean,
    onShowDatePickerChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Basic Information Card
        Text("Edit Expense", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Expense Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = {
                        Text(
                            text = "$",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker
                OutlinedButton(
                    onClick = { onShowDatePickerChange(true) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select Date",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Selection Card
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
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(170.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ExpenseCategory.entries.toTypedArray()) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = selectedCategory == category,
                            onSelected = { onCategoryChange(category) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recurrence Settings Card
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
                        text = "Recurring Expense",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = onRecurringChange
                    )
                }

                if (isRecurring) {
                    Spacer(modifier = Modifier.height(16.dp))
                    RecurrenceSelector(
                        selectedRecurrence = recurrenceType,
                        onRecurrenceSelected = onRecurrenceTypeChange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amountValue > 0) {
                        val updatedExpense = expense.copy(
                            title = title,
                            amount = amountValue,
                            category = selectedCategory,
                            notes = notes,
                            date = date,
                            isRecurring = isRecurring,
                            recurrenceType = if (isRecurring) recurrenceType else null
                        )
                        onUpdateExpense(updatedExpense)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Update Expense")
            }

            OutlinedButton(
                onClick = { onShowDeleteDialogChange(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Expense")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Simple Date Picker (you can enhance this with a proper date picker dialog)
    if (showDatePicker) {
        CustomDatePickerDialog(
            initialDateTime = date,
            onDismiss = { onShowDatePickerChange(false) },
            onDateSelected = { selectedDate ->
                onDateChange(selectedDate)
                onShowDatePickerChange(false)
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onShowDeleteDialogChange(false) },
            title = { Text("Delete Expense") },
            text = {
                Text("Are you sure you want to delete this expense? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteExpense(expense)
                        onShowDeleteDialogChange(false)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { onShowDeleteDialogChange(false) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceSelector(
    selectedRecurrence: RecurrenceType,
    onRecurrenceSelected: (RecurrenceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Repeat",
            style = MaterialTheme.typography.labelMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RecurrenceType.entries.forEach { recurrence ->
                Card(
                    onClick = { onRecurrenceSelected(recurrence) },
                    modifier = Modifier.padding(2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRecurrence == recurrence) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Text(
                        text = when (recurrence) {
                            RecurrenceType.DAILY -> "Daily"
                            RecurrenceType.WEEKLY -> "Weekly"
                            RecurrenceType.MONTHLY -> "Monthly"
                            RecurrenceType.YEARLY -> "Yearly"
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

// Extension function for formatting LocalDateTime
fun LocalDateTime.format(formatter: DateTimeFormatter): String {
    return formatter.format(this)
}