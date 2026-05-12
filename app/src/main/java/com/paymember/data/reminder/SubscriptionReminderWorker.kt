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
import com.paymember.data.db.AppDatabase
import com.paymember.data.model.BillingPeriod

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
            val periodText = if (subscription.period == BillingPeriod.MONTHLY) "mensual" else "anual"
            val noticeText = if (subscription.reminderDaysBefore == 0) {
                "vence hoy"
            } else {
                "vence en ${subscription.reminderDaysBefore} dias"
            }
            val content = "${subscription.serviceName}: ${subscription.price} EUR ($periodText), $noticeText"
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Recordatorio de pago")
                .setContentText(content)
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
