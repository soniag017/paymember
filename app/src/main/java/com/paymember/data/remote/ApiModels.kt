package com.paymember.data.remote

import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity

data class AuthRequest(
    val email: String,
    val password: String
)

data class GoogleAuthRequest(
    val idToken: String
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val email: String
)

data class SubscriptionPayload(
    val id: Long? = null,
    val serviceName: String,
    val price: Double,
    val billingDay: Int,
    val period: BillingPeriod,
    val reminderEnabled: Boolean,
    val reminderDaysBefore: Int,
    val notes: String?,
    val startDate: String?
) {
    fun toEntity(): SubscriptionEntity = SubscriptionEntity(
        id = (id ?: 0L).toInt(),
        serviceName = serviceName,
        price = price,
        billingDay = billingDay,
        period = period,
        reminderEnabled = reminderEnabled,
        reminderDaysBefore = reminderDaysBefore,
        notes = notes,
        startDate = startDate.orEmpty()
    )
}

fun SubscriptionEntity.toPayload(): SubscriptionPayload = SubscriptionPayload(
    id = if (id == 0) null else id.toLong(),
    serviceName = serviceName,
    price = price,
    billingDay = billingDay,
    period = period,
    reminderEnabled = reminderEnabled,
    reminderDaysBefore = reminderDaysBefore,
    notes = notes,
    startDate = startDate.ifBlank { null }
)
