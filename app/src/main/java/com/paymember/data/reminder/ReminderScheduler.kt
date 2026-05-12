package com.paymember.data.reminder

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME_PREFIX = "subscription_reminder_"

    fun schedule(context: Context, subscription: SubscriptionEntity) {
        if (!subscription.reminderEnabled) {
            cancel(context, subscription.id)
            return
        }

        val delay = calculateInitialDelay(
            subscription.billingDay,
            subscription.period,
            subscription.reminderDaysBefore
        )
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

    private fun calculateInitialDelay(
        billingDay: Int,
        period: BillingPeriod,
        reminderDaysBefore: Int
    ): Duration {
        val now = LocalDateTime.now()
        val nextRun = nextTriggerDateTime(now, billingDay, period, reminderDaysBefore)
        return Duration.between(now, nextRun)
    }

    private fun nextTriggerDateTime(
        now: LocalDateTime,
        billingDay: Int,
        period: BillingPeriod,
        reminderDaysBefore: Int
    ): LocalDateTime {
        val time = LocalTime.of(9, 0)
        val today = now.toLocalDate()

        val candidate = when (period) {
            BillingPeriod.MONTHLY -> dateForMonth(today.year, today.monthValue, billingDay)
            BillingPeriod.YEARLY -> dateForYear(today.year, today.monthValue, billingDay)
        }.minusDays(reminderDaysBefore.toLong()).atTime(time)

        if (candidate.isAfter(now)) {
            return candidate
        }

        return when (period) {
            BillingPeriod.MONTHLY -> {
                val nextMonth = today.plusMonths(1)
                dateForMonth(nextMonth.year, nextMonth.monthValue, billingDay)
                    .minusDays(reminderDaysBefore.toLong())
                    .atTime(time)
            }
            BillingPeriod.YEARLY -> dateForYear(today.year + 1, today.monthValue, billingDay)
                .minusDays(reminderDaysBefore.toLong())
                .atTime(time)
        }
    }

    private fun dateForMonth(year: Int, month: Int, billingDay: Int): LocalDate {
        val yearMonth = YearMonth.of(year, month)
        val safeDay = billingDay.coerceIn(1, yearMonth.lengthOfMonth())
        return yearMonth.atDay(safeDay)
    }

    private fun dateForYear(year: Int, month: Int, billingDay: Int): LocalDate {
        val yearMonth = YearMonth.of(year, month)
        val safeDay = billingDay.coerceIn(1, yearMonth.lengthOfMonth())
        return yearMonth.atDay(safeDay)
    }
}
