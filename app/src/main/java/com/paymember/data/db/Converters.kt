package com.paymember.data.db

import androidx.room.TypeConverter
import com.paymember.data.model.BillingPeriod

class Converters {
    @TypeConverter
    fun fromBillingPeriod(period: BillingPeriod): String = period.name

    @TypeConverter
    fun toBillingPeriod(value: String): BillingPeriod = BillingPeriod.valueOf(value)
}
