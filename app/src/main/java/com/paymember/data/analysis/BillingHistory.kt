package com.paymember.data.analysis

import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeParseException

data class BillingCharge(
    val date: LocalDate,
    val amount: Double
)

data class BillingHistory(
    val charges: List<BillingCharge>,
    val totalSpent: Double
) {
    val chargeCount: Int = charges.size
}

object BillingHistoryCalculator {
    fun calculate(
        subscription: SubscriptionEntity,
        today: LocalDate = LocalDate.now()
    ): BillingHistory {
        val startDate = subscription.startLocalDate(today)
        if (startDate.isAfter(today)) return BillingHistory(emptyList(), 0.0)

        val charges = when (subscription.period) {
            BillingPeriod.MONTHLY -> monthlyCharges(subscription, startDate, today)
            BillingPeriod.YEARLY -> yearlyCharges(subscription, startDate, today)
        }

        return BillingHistory(
            charges = charges,
            totalSpent = charges.sumOf { it.amount }
        )
    }

    fun nextChargeDate(
        subscription: SubscriptionEntity,
        today: LocalDate = LocalDate.now()
    ): LocalDate {
        val startDate = subscription.startLocalDate(today)
        val safeDay = subscription.billingDay.coerceIn(1, 31)

        if (subscription.period == BillingPeriod.MONTHLY) {
            var month = YearMonth.from(today)
            repeat(24) {
                val date = month.atSafeDay(safeDay)
                if (!date.isBefore(today) && !date.isBefore(startDate)) return date
                month = month.plusMonths(1)
            }
        } else {
            val billingMonth = startDate.month
            var year = today.year
            repeat(10) {
                val date = YearMonth.of(year, billingMonth).atSafeDay(safeDay)
                if (!date.isBefore(today) && !date.isBefore(startDate)) return date
                year += 1
            }
        }

        return today
    }

    fun chargeDateForMonth(subscription: SubscriptionEntity, month: YearMonth): LocalDate? {
        val startDate = subscription.startLocalDate(LocalDate.now())
        val safeDay = subscription.billingDay.coerceIn(1, 31)

        if (subscription.period == BillingPeriod.YEARLY && month.month != startDate.month) {
            return null
        }

        val date = month.atSafeDay(safeDay)
        return if (date.isBefore(startDate)) null else date
    }

    fun SubscriptionEntity.startLocalDate(fallback: LocalDate = LocalDate.now()): LocalDate {
        if (startDate.isBlank()) return fallback
        return try {
            LocalDate.parse(startDate)
        } catch (_: DateTimeParseException) {
            fallback
        }
    }

    private fun monthlyCharges(
        subscription: SubscriptionEntity,
        startDate: LocalDate,
        today: LocalDate
    ): List<BillingCharge> {
        val safeDay = subscription.billingDay.coerceIn(1, 31)
        val charges = mutableListOf<BillingCharge>()
        var month = YearMonth.from(startDate)
        val endMonth = YearMonth.from(today)

        while (!month.isAfter(endMonth)) {
            val date = month.atSafeDay(safeDay)
            if (!date.isBefore(startDate) && !date.isAfter(today)) {
                charges += BillingCharge(date = date, amount = subscription.price)
            }
            month = month.plusMonths(1)
        }

        return charges.sortedByDescending { it.date }
    }

    private fun yearlyCharges(
        subscription: SubscriptionEntity,
        startDate: LocalDate,
        today: LocalDate
    ): List<BillingCharge> {
        val safeDay = subscription.billingDay.coerceIn(1, 31)
        val charges = mutableListOf<BillingCharge>()
        var year = startDate.year

        while (year <= today.year) {
            val date = YearMonth.of(year, startDate.month).atSafeDay(safeDay)
            if (!date.isBefore(startDate) && !date.isAfter(today)) {
                charges += BillingCharge(date = date, amount = subscription.price)
            }
            year += 1
        }

        return charges.sortedByDescending { it.date }
    }

    private fun YearMonth.atSafeDay(day: Int): LocalDate {
        return atDay(day.coerceIn(1, lengthOfMonth()))
    }
}
