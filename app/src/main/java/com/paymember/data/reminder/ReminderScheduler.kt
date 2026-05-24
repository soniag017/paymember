package com.paymember.data.reminder

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.paymember.data.model.SubscriptionEntity
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME_PREFIX = "subscription_reminder_"

    fun schedule(context: Context, subscription: SubscriptionEntity) {
        if (!subscription.reminderEnabled) {
            cancel(context, subscription.id)
            return
        }

        val now = LocalDateTime.now()
        val nextReminder = ReminderTiming.nextReminder(subscription, now) ?: run {
            cancel(context, subscription.id)
            return
        }
        val delay = Duration.between(now, nextReminder.triggerAt)
            .takeUnless { it.isNegative }
            ?: Duration.ZERO
        val input = Data.Builder()
            .putInt(SubscriptionReminderWorker.KEY_SUBSCRIPTION_ID, subscription.id)
            .build()

        val request = OneTimeWorkRequestBuilder<SubscriptionReminderWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(input)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workName(subscription.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(context: Context, subscriptionId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(subscriptionId))
    }

    private fun workName(subscriptionId: Int): String = "$WORK_NAME_PREFIX$subscriptionId"
}
