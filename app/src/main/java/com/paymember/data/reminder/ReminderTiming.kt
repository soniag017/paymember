package com.paymember.data.reminder

import com.paymember.data.analysis.BillingHistoryCalculator
import com.paymember.data.model.SubscriptionEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ReminderSchedule(
    val triggerAt: LocalDateTime,
    val chargeDate: LocalDate
)

object ReminderTiming {
    private val reminderTime: LocalTime = LocalTime.of(9, 0)

    fun nextReminder(
        subscription: SubscriptionEntity,
        now: LocalDateTime = LocalDateTime.now()
    ): ReminderSchedule? {
        if (!subscription.reminderEnabled) return null

        val daysBefore = subscription.reminderDaysBefore.coerceIn(0, 30).toLong()
        var searchDate = now.toLocalDate()

        repeat(48) {
            val chargeDate = BillingHistoryCalculator.nextChargeDate(subscription, searchDate)
            val triggerAt = chargeDate.minusDays(daysBefore).atTime(reminderTime)

            if (triggerAt.isAfter(now)) {
                return ReminderSchedule(
                    triggerAt = triggerAt,
                    chargeDate = chargeDate
                )
            }

            searchDate = chargeDate.plusDays(1)
        }

        return null
    }
}
