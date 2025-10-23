package com.example.expensetracker.domain.usecase

import android.content.Context
import com.example.expensetracker.domain.service.ExportService
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

class ExportExpensesToCsvUseCase @Inject constructor(
    private val exportService: ExportService
) {
    suspend operator fun invoke(startDate: LocalDateTime, endDate: LocalDateTime): String {
        return exportService.exportExpensesToCsv(startDate, endDate)
    }
}

class ExportBudgetsToCsvUseCase @Inject constructor(
    private val exportService: ExportService
) {
    suspend operator fun invoke(): String {
        return exportService.exportBudgetsToCsv()
    }
}

class GeneratePdfReportUseCase @Inject constructor(
    private val exportService: ExportService
) {
    suspend operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        context: Context
    ): File {
        return exportService.generateExpensePdf(startDate, endDate, context)
    }
}