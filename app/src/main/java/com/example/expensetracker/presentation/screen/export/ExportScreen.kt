package com.example.expensetracker.presentation.screen.export

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.presentation.component.CustomDatePickerDialog
import com.example.expensetracker.presentation.viewmodel.DateRangePreset
import com.example.expensetracker.presentation.viewmodel.ExportType
import com.example.expensetracker.presentation.viewmodel.ExportViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onBack: () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val exportState by viewModel.exportState.collectAsState()
    val dateRangeState by viewModel.dateRangeState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle export results
    LaunchedEffect(exportState.exportedContent, exportState.exportedFile) {
        when (exportState.exportType) {
            ExportType.CSV_EXPENSES -> {
                exportState.exportedContent?.let { content ->
                    shareText(context, content, "expenses.csv", "text/csv")
                    viewModel.clearExportState()
                }
            }

            ExportType.CSV_BUDGETS -> {
                exportState.exportedContent?.let { content ->
                    shareText(context, content, "budgets.csv", "text/csv")
                    viewModel.clearExportState()
                }
            }

            ExportType.PDF_REPORT -> {
                exportState.exportedFile?.let { file ->
                    shareFile(context, file, "application/pdf")
                    viewModel.clearExportState()
                }
            }

            else -> {}
        }
    }

    // Handle errors
    LaunchedEffect(exportState.error) {
        exportState.error?.let { error ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearExportState()
            }
        }
    }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Date Range Card with Date Pickers
        DateRangeCard(
            startDate = dateRangeState.startDate,
            endDate = dateRangeState.endDate,
            onStartDateClick = { viewModel.showStartDatePicker() },
            onEndDateClick = { viewModel.showEndDatePicker() },
            onPresetSelected = { preset -> viewModel.setDateRangePreset(preset) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Export Options
        Text(
            text = "Export Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // CSV Export Buttons
        ExportOptionCard(
            title = "Export Expenses to CSV",
            description = "Download expenses as CSV file",
            icon = Icons.Default.Download,
            isLoading = exportState.isExporting && exportState.exportType == ExportType.CSV_EXPENSES,
            onClick = { viewModel.exportExpensesToCsv() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExportOptionCard(
            title = "Export Budgets to CSV",
            description = "Download budgets as CSV file",
            icon = Icons.Default.Download,
            isLoading = exportState.isExporting && exportState.exportType == ExportType.CSV_BUDGETS,
            onClick = { viewModel.exportBudgetsToCsv() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PDF Export Button
        ExportOptionCard(
            title = "Generate PDF Report",
            description = "Create a detailed PDF expense report",
            icon = Icons.Default.PictureAsPdf,
            isLoading = exportState.isExporting && exportState.exportType == ExportType.PDF_REPORT,
            onClick = { viewModel.generatePdfReport(context) }
        )
    }


    // Date Pickers
    if (dateRangeState.showStartDatePicker) {
        CustomDatePickerDialog(
            initialDateTime = dateRangeState.startDate,
            onDismiss = { viewModel.hideDatePickers() },
            onDateSelected = { selectedDate ->
                viewModel.updateStartDate(selectedDate)
            }
        )
    }

    if (dateRangeState.showEndDatePicker) {
        CustomDatePickerDialog(
            initialDateTime = dateRangeState.endDate,
            onDismiss = { viewModel.hideDatePickers() },
            onDateSelected = { selectedDate ->
                viewModel.updateEndDate(selectedDate)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeCard(
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onPresetSelected: (DateRangePreset) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
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
                    text = "Date Range",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Quick Presets Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.menuAnchor()
                    ) {
                        Text("Quick Range")
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Last 7 days") },
                            onClick = {
                                onPresetSelected(DateRangePreset.LAST_7_DAYS)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Last 30 days") },
                            onClick = {
                                onPresetSelected(DateRangePreset.LAST_30_DAYS)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("This month") },
                            onClick = {
                                onPresetSelected(DateRangePreset.THIS_MONTH)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Last month") },
                            onClick = {
                                onPresetSelected(DateRangePreset.LAST_MONTH)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("This year") },
                            onClick = {
                                onPresetSelected(DateRangePreset.THIS_YEAR)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Date
            DateSelectionRow(
                label = "From",
                date = startDate,
                onClick = onStartDateClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // End Date
            DateSelectionRow(
                label = "To",
                date = endDate,
                onClick = onEndDateClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date range summary
            Text(
                text = "Selected range: ${calculateDateRangeDays(startDate, endDate)} days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DateSelectionRow(
    label: String,
    date: LocalDateTime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("EEEE")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Select date",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ExportOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = !isLoading,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }
        }
    }
}

// Helper functions
private fun calculateDateRangeDays(start: LocalDateTime, end: LocalDateTime): Long {
    return java.time.Duration.between(start, end).toDays()
}

fun shareText(context: Context, text: String, fileName: String, mimeType: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, fileName)
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

fun shareFile(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}