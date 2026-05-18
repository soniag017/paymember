package com.paymember.data.analysis

import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class BillingHistoryCalculatorTest {
    @Test
    fun calculate_monthlySubscription_returnsPastChargesAndTotal() {
        val subscription = SubscriptionEntity(
            serviceName = "Netflix",
            price = 10.0,
            billingDay = 5,
            period = BillingPeriod.MONTHLY,
            reminderEnabled = true,
            notes = null,
            startDate = "2026-01-01"
        )

        val history = BillingHistoryCalculator.calculate(
            subscription = subscription,
            today = LocalDate.of(2026, 3, 20)
        )

        assertEquals(3, history.chargeCount)
        assertEquals(30.0, history.totalSpent, 0.0)
        assertEquals(LocalDate.of(2026, 3, 5), history.charges.first().date)
    }

    @Test
    fun calculate_yearlySubscription_usesStartMonthAsRenewalMonth() {
        val subscription = SubscriptionEntity(
            serviceName = "Disney+",
            price = 120.0,
            billingDay = 20,
            period = BillingPeriod.YEARLY,
            reminderEnabled = true,
            notes = null,
            startDate = "2024-05-20"
        )

        val history = BillingHistoryCalculator.calculate(
            subscription = subscription,
            today = LocalDate.of(2026, 5, 21)
        )

        assertEquals(3, history.chargeCount)
        assertEquals(360.0, history.totalSpent, 0.0)
        assertEquals(LocalDate.of(2026, 5, 20), history.charges.first().date)
    }
}
