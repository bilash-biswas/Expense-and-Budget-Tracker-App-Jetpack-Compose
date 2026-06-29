package com.example.expensetracker.presentation.screen.expense

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.saveable.rememberSaveable
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
    onBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val expense = viewModel.getExpenseById(expenseId)

    var title by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(ExpenseCategory.FOOD) }
    var notes by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(LocalDateTime.now()) }
    var isRecurring by rememberSaveable { mutableStateOf(false) }
    var recurrenceType by rememberSaveable { mutableStateOf(RecurrenceType.MONTHLY) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(expense) {
        if (expense != null) {
            title = expense.title
            amount = expense.amount.toString()
            selectedCategory = expense.category
            notes = expense.notes ?: ""
            date = expense.date
            isRecurring = expense.isRecurring
            recurrenceType = expense.recurrenceType ?: RecurrenceType.MONTHLY
        }
    }

    if (expense == null && expenseId != 0L) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading expense...", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    } else if (expense != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Expense", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Expense",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
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
                        onBack()
                    }
                },
                onDeleteExpense = { expenseToDelete ->
                    coroutineScope.launch {
                        viewModel.deleteExpense(expenseToDelete)
                        snackbarHostState.showSnackbar("Expense deleted successfully")
                        onBack()
                    }
                },
                showDeleteDialog = showDeleteDialog,
                onShowDeleteDialogChange = { showDeleteDialog = it },
                showDatePicker = showDatePicker,
                onShowDatePickerChange = { showDatePicker = it },
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Expense not found", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

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
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    text = "Transaction Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

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
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                OutlinedButton(
                    onClick = { onShowDatePickerChange(true) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Select Date",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy  HH:mm")),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
            }
        }

        // Category selection
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

        // Recurrence Settings
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Recurring Transaction",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Automate repeated payments",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = onRecurringChange
                    )
                }

                if (isRecurring) {
                    RecurrenceSelector(
                        selectedRecurrence = recurrenceType,
                        onRecurrenceSelected = onRecurrenceTypeChange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Save Button
        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                if (title.isNotBlank() && amountValue > 0) {
                    val updatedExpense = expense.copy(
                        title = title.trim(),
                        amount = amountValue,
                        category = selectedCategory,
                        notes = notes.trim(),
                        date = date,
                        isRecurring = isRecurring,
                        recurrenceType = if (isRecurring) recurrenceType else null
                    )
                    onUpdateExpense(updatedExpense)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = title.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Save Changes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onShowDeleteDialogChange(false) },
            title = { Text("Delete Transaction", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this expense? This action is permanent and cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteExpense(expense)
                        onShowDeleteDialogChange(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onShowDeleteDialogChange(false) }) {
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
            text = "Repeat Interval",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RecurrenceType.entries.forEach { recurrence ->
                val isSelected = selectedRecurrence == recurrence
                val tintColor = MaterialTheme.colorScheme.primary
                
                Card(
                    onClick = { onRecurrenceSelected(recurrence) },
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.5.dp,
                            color = if (isSelected) tintColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) tintColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (recurrence) {
                                RecurrenceType.DAILY -> "Daily"
                                RecurrenceType.WEEKLY -> "Weekly"
                                RecurrenceType.MONTHLY -> "Monthly"
                                RecurrenceType.YEARLY -> "Yearly"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}