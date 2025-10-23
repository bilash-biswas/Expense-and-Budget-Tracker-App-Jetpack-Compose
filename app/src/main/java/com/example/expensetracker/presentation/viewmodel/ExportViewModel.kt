package com.example.expensetracker.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.usecase.ExportBudgetsToCsvUseCase
import com.example.expensetracker.domain.usecase.ExportExpensesToCsvUseCase
import com.example.expensetracker.domain.usecase.GeneratePdfReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportExpensesToCsvUseCase: ExportExpensesToCsvUseCase,
    private val exportBudgetsToCsvUseCase: ExportBudgetsToCsvUseCase,
    private val generatePdfReportUseCase: GeneratePdfReportUseCase
) : ViewModel() {

    private val _exportState = MutableStateFlow(ExportState())
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _dateRangeState = MutableStateFlow(DateRangeState())
    val dateRangeState: StateFlow<DateRangeState> = _dateRangeState.asStateFlow()

    // Date picker actions
    fun showStartDatePicker() {
        _dateRangeState.update { it.copy(showStartDatePicker = true) }
    }

    fun showEndDatePicker() {
        _dateRangeState.update { it.copy(showEndDatePicker = true) }
    }

    fun hideDatePickers() {
        _dateRangeState.update {
            it.copy(
                showStartDatePicker = false,
                showEndDatePicker = false
            )
        }
    }

    fun updateStartDate(date: LocalDateTime) {
        _dateRangeState.update {
            it.copy(
                startDate = date,
                showStartDatePicker = false
            )
        }
    }

    fun updateEndDate(date: LocalDateTime) {
        _dateRangeState.update {
            it.copy(
                endDate = date,
                showEndDatePicker = false
            )
        }
    }

    fun updateDateRange(startDate: LocalDateTime, endDate: LocalDateTime) {
        _dateRangeState.update {
            DateRangeState(
                startDate = startDate,
                endDate = endDate
            )
        }
    }

    // Quick date range presets
    fun setDateRangePreset(preset: DateRangePreset) {
        val now = LocalDateTime.now()
        val newRange = when (preset) {
            DateRangePreset.LAST_7_DAYS ->
                now.minusDays(7) to now
            DateRangePreset.LAST_30_DAYS ->
                now.minusDays(30) to now
            DateRangePreset.THIS_MONTH ->
                now.withDayOfMonth(1).withHour(0).withMinute(0) to now
            DateRangePreset.LAST_MONTH -> {
                val firstDayLastMonth = now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0)
                val lastDayLastMonth = now.withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59)
                firstDayLastMonth to lastDayLastMonth
            }
            DateRangePreset.THIS_YEAR ->
                now.withDayOfYear(1).withHour(0).withMinute(0) to now
        }
        _dateRangeState.update {
            it.copy(
                startDate = newRange.first,
                endDate = newRange.second
            )
        }
    }

    // Export functions using the current date range
    fun exportExpensesToCsv() {
        viewModelScope.launch {
            _exportState.update { it.copy(isExporting = true, error = null) }
            try {
                val csvContent = exportExpensesToCsvUseCase(
                    _dateRangeState.value.startDate,
                    _dateRangeState.value.endDate
                )
                _exportState.update {
                    it.copy(
                        isExporting = false,
                        exportedContent = csvContent,
                        exportType = ExportType.CSV_EXPENSES
                    )
                }
            } catch (e: Exception) {
                _exportState.update {
                    it.copy(
                        isExporting = false,
                        error = "Failed to export expenses: ${e.message}"
                    )
                }
            }
        }
    }

    fun exportBudgetsToCsv() {
        viewModelScope.launch {
            _exportState.update { it.copy(isExporting = true, error = null) }
            try {
                val csvContent = exportBudgetsToCsvUseCase()
                _exportState.update {
                    it.copy(
                        isExporting = false,
                        exportedContent = csvContent,
                        exportType = ExportType.CSV_BUDGETS
                    )
                }
            } catch (e: Exception) {
                _exportState.update {
                    it.copy(
                        isExporting = false,
                        error = "Failed to export budgets: ${e.message}"
                    )
                }
            }
        }
    }

    fun generatePdfReport(context: Context) {
        viewModelScope.launch {
            _exportState.update { it.copy(isExporting = true, error = null) }
            try {
                val pdfFile = generatePdfReportUseCase(
                    _dateRangeState.value.startDate,
                    _dateRangeState.value.endDate,
                    context
                )
                _exportState.update {
                    it.copy(
                        isExporting = false,
                        exportedFile = pdfFile,
                        exportType = ExportType.PDF_REPORT
                    )
                }
            } catch (e: Exception) {
                _exportState.update {
                    it.copy(
                        isExporting = false,
                        error = "Failed to generate PDF: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearExportState() {
        _exportState.update { ExportState() }
    }
}

data class ExportState(
    val isExporting: Boolean = false,
    val exportedContent: String? = null,
    val exportedFile: File? = null,
    val exportType: ExportType? = null,
    val error: String? = null
)

data class DateRangeState(
    val startDate: LocalDateTime = LocalDateTime.now().minusMonths(1),
    val endDate: LocalDateTime = LocalDateTime.now(),
    val showStartDatePicker: Boolean = false,
    val showEndDatePicker: Boolean = false
)

enum class ExportType {
    CSV_EXPENSES, CSV_BUDGETS, PDF_REPORT
}

enum class DateRangePreset {
    LAST_7_DAYS, LAST_30_DAYS, THIS_MONTH, LAST_MONTH, THIS_YEAR
}