package com.example.expensetracker.domain.service

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Color
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.domain.repository.BudgetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExportService @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
) {

    suspend fun exportExpensesToCsv(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): String = withContext(Dispatchers.IO) {
        val expenses = expenseRepository.getExpensesByDateRange(startDate, endDate)

        val csvHeader = "Date,Title,Amount,Category,Notes,Recurring\n"
        val csvRows = expenses?.joinToString("\n") { expense ->
            listOf(
                expense.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                escapeCsvField(expense.title),
                expense.amount.toString(),
                escapeCsvField(expense.category.displayName),
                escapeCsvField(expense.notes ?: ""),
                expense.isRecurring.toString()
            ).joinToString(",")
        }

        return@withContext csvHeader + csvRows
    }

    suspend fun exportBudgetsToCsv(): String = withContext(Dispatchers.IO) {
        val budgets = budgetRepository.getActiveBudgets()

        val csvHeader = "Category,Budget Amount,Period,Start Date,End Date,Active\n"
        val csvRows = budgets.joinToString("\n") { budget ->
            listOf(
                escapeCsvField(budget.category.displayName),
                budget.amount.toString(),
                budget.period.name,
                budget.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                budget.endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                budget.isActive.toString()
            ).joinToString(",")
        }

        return@withContext csvHeader + csvRows
    }

    suspend fun generateExpensePdf(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        context: Context
    ): File = withContext(Dispatchers.IO) {
        val expenses = expenseRepository.getExpensesByDateRange(startDate, endDate)

        // Create PDF document
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var yPosition = 50f

        // Title
        val titlePaint = Paint().apply {
            textSize = 18f
            color = Color.BLACK
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Expense Report", 297.5f, yPosition, titlePaint)
        yPosition += 40f

        // Date range
        val normalPaint = Paint().apply {
            textSize = 12f
            color = Color.BLACK
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText(
            "Period: ${startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))} to ${
                endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            }",
            50f,
            yPosition,
            normalPaint
        )
        yPosition += 30f

        // Summary
        val totalAmount = expenses?.sumOf { it.amount }
        canvas.drawText("Total Expenses: $${"%.2f".format(totalAmount)}", 50f, yPosition, normalPaint)
        yPosition += 20f

        canvas.drawText("Number of Expenses: ${expenses?.size}", 50f, yPosition, normalPaint)
        yPosition += 40f

        // Expenses table header
        val headerPaint = Paint().apply {
            textSize = 10f
            color = Color.BLACK
            isFakeBoldText = true
        }
        canvas.drawText("Date", 50f, yPosition, headerPaint)
        canvas.drawText("Title", 120f, yPosition, headerPaint)
        canvas.drawText("Amount", 300f, yPosition, headerPaint)
        canvas.drawText("Category", 380f, yPosition, headerPaint)
        yPosition += 20f

        // Draw line
        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
        }
        canvas.drawLine(50f, yPosition - 5f, 545f, yPosition - 5f, linePaint)
        yPosition += 10f

        // Expenses table rows
        val rowPaint = Paint().apply {
            textSize = 9f
            color = Color.BLACK
        }

        expenses?.forEach { expense ->
            if (yPosition > 800f) { // Start new page if needed
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                yPosition = 50f
            }

            canvas.drawText(
                expense.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                50f,
                yPosition,
                rowPaint
            )
            canvas.drawText(
                truncateText(expense.title, 25),
                120f,
                yPosition,
                rowPaint
            )
            canvas.drawText(
                "$${"%.2f".format(expense.amount)}",
                300f,
                yPosition,
                rowPaint
            )
            canvas.drawText(
                truncateText(expense.category.displayName, 15),
                380f,
                yPosition,
                rowPaint
            )
            yPosition += 15f
        }

        document.finishPage(page)

        // Save to file
        val fileName = "expense_report_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)
        document.writeTo(FileOutputStream(file))
        document.close()

        return@withContext file
    }

    private fun escapeCsvField(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }

    private fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.substring(0, maxLength - 3) + "..."
        } else {
            text
        }
    }
}