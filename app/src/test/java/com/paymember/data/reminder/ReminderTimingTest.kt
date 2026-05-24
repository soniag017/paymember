package com.paymember.data.reminder

import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReminderTimingTest {

    @Test
    fun nextReminderUsesConfiguredDaysBeforeCharge() {
        val subscription = subscription(
            billingDay = 27,
            reminderDaysBefore = 2
        )

        val reminder = ReminderTiming.nextReminder(
            subscription,
            now = LocalDateTime.of(2026, 5, 24, 17, 0)
        )

        assertEquals(LocalDateTime.of(2026, 5, 25, 9, 0), reminder?.triggerAt)
        assertEquals(LocalDate.of(2026, 5, 27), reminder?.chargeDate)
    }

    @Test
    fun nextReminderSkipsPastTriggerForSameCharge() {
        val subscription = subscription(
            billingDay = 25,
            reminderDaysBefore = 1
        )

        val reminder = ReminderTiming.nextReminder(
            subscription,
            now = LocalDateTime.of(2026, 5, 24, 17, 0)
        )

        assertEquals(LocalDateTime.of(2026, 6, 24, 9, 0), reminder?.triggerAt)
        assertEquals(LocalDate.of(2026, 6, 25), reminder?.chargeDate)
    }

    @Test
    fun nextReminderUsesStartMonthForYearlySubscriptions() {
        val subscription = subscription(
            period = BillingPeriod.YEARLY,
            billingDay = 10,
            reminderDaysBefore = 3,
            startDate = "2026-08-10"
        )

        val reminder = ReminderTiming.nextReminder(
            subscription,
            now = LocalDateTime.of(2026, 5, 24, 17, 0)
        )

        assertEquals(LocalDateTime.of(2026, 8, 7, 9, 0), reminder?.triggerAt)
        assertEquals(LocalDate.of(2026, 8, 10), reminder?.chargeDate)
    }

    @Test
    fun nextReminderIgnoresDisabledSubscriptions() {
        val subscription = subscription(reminderEnabled = false)

        assertNull(ReminderTiming.nextReminder(subscription))
    }

    private fun subscription(
        period: BillingPeriod = BillingPeriod.MONTHLY,
        billingDay: Int = 1,
        reminderEnabled: Boolean = true,
        reminderDaysBefore: Int = 1,
        startDate: String = "2026-01-01"
    ): SubscriptionEntity {
        return SubscriptionEntity(
            serviceName = "Netflix",
            price = 12.99,
            billingDay = billingDay,
            period = period,
            reminderEnabled = reminderEnabled,
            reminderDaysBefore = reminderDaysBefore,
            notes = null,
            startDate = startDate
        )
    }
}
