package com.example.expensetracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expensetracker.MainActivity
import com.example.expensetracker.R
import com.example.expensetracker.domain.usecase.budget.GetBudgetAlertsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.math.roundToInt

@HiltWorker
class BudgetAlertWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getBudgetAlertsUseCase: GetBudgetAlertsUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val alerts = getBudgetAlertsUseCase()

            alerts.forEachIndexed { index, alert ->
                val percentText = (alert.percentageUsed * 100).roundToInt()
                val title = if (alert.isOverBudget) {
                    "⚠️ Budget Exceeded: ${alert.budget.category.displayName}"
                } else {
                    "🔔 Budget Alert: ${alert.budget.category.displayName}"
                }
                val message = if (alert.isOverBudget) {
                    "You are over your ${alert.budget.category.displayName} budget by " +
                        "৳${"%.0f".format(alert.currentSpending - alert.budget.amount)}!"
                } else {
                    "You've used $percentText% of your ${alert.budget.category.displayName} budget. " +
                        "${alert.daysRemaining} day(s) remaining."
                }
                sendNotification(notificationId = NOTIFICATION_BASE_ID + index, title = title, message = message)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun sendNotification(notificationId: Int, title: String, message: String) {
        // Create deep-link intent back to the app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not granted — silently ignore
        }
    }

    companion object {
        const val CHANNEL_ID = "budget_alerts_channel"
        const val CHANNEL_NAME = "Budget Alerts"
        const val CHANNEL_DESC = "Notifications for budget threshold alerts"
        const val WORK_NAME = "budget_alert_periodic_work"
        const val NOTIFICATION_BASE_ID = 1000

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC
                    enableVibration(true)
                }
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }
        }
    }
}
