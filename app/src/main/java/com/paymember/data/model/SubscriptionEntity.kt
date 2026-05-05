package com.paymember.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceName: String,
    val price: Double,
    val billingDay: Int,
    val period: BillingPeriod,
    val reminderEnabled: Boolean,
    val notes: String?
)
