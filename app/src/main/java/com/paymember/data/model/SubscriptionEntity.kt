package com.paymember.data.model

import androidx.room.Entity
import androidx.room.ColumnInfo
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
    @ColumnInfo(defaultValue = "0")
    val reminderDaysBefore: Int = 0,
    val notes: String?
)
