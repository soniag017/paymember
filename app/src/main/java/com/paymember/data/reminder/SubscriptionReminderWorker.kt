package com.paymember.data.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paymember.R
import com.paymember.data.analysis.BillingHistoryCalculator
import com.paymember.data.db.AppDatabase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

class SubscriptionReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val subscriptionId = inputData.getInt(KEY_SUBSCRIPTION_ID, 0)
        if (subscriptionId == 0) return Result.success()

        val dao = AppDatabase.getInstance(applicationContext).subscriptionDao()
        val subscription = dao.getById(subscriptionId) ?: return Result.success()

        if (!subscription.reminderEnabled) {
            ReminderScheduler.cancel(applicationContext, subscriptionId)
            return Result.success()
        }

        createChannelIfNeeded()

        if (hasNotificationPermission()) {
            val nextCharge = BillingHistoryCalculator.nextChargeDate(subscription)
            val daysUntilCharge = ChronoUnit.DAYS.between(LocalDate.now(), nextCharge)
                .coerceAtLeast(0)
            val content = buildNotificationText(
                serviceName = subscription.serviceName.cleanServiceName(),
                price = subscription.price,
                daysUntilCharge = daysUntilCharge
            )
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Recordatorio de cobro")
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext)
                .notify(subscriptionId, notification)
        }

        ReminderScheduler.schedule(applicationContext, subscription)
        return Result.success()
    }

    private fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun buildNotificationText(
        serviceName: String,
        price: Double,
        daysUntilCharge: Long
    ): String {
        val dayText = when (daysUntilCharge) {
            0L -> "hoy"
            1L -> "mañana"
            else -> "en $daysUntilCharge días"
        }
        val amount = String.format(Locale.getDefault(), "%.2f", price)
        return "Se va a cobrar el servicio $serviceName $dayText. Importe: $amount EUR."
    }

    private fun String.cleanServiceName(): String = substringBefore(" - ").ifBlank { this }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = applicationContext.getString(R.string.reminder_channel_description)
        }

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val KEY_SUBSCRIPTION_ID = "subscription_id"
        private const val CHANNEL_ID = "paymember_reminders"
    }
}
