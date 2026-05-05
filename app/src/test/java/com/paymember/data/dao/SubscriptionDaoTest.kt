package com.paymember.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.paymember.data.db.AppDatabase
import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SubscriptionDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: SubscriptionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.subscriptionDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndReadSubscription() = runBlocking {
        val id = dao.insert(
            SubscriptionEntity(
                serviceName = "Netflix",
                price = 12.99,
                billingDay = 10,
                period = BillingPeriod.MONTHLY,
                reminderEnabled = true,
                notes = "Plan estandar"
            )
        ).toInt()

        val result = dao.getById(id)

        assertEquals("Netflix", result?.serviceName)
        assertEquals(12.99, result?.price)
        assertEquals(10, result?.billingDay)
        assertEquals(BillingPeriod.MONTHLY, result?.period)
        assertEquals(true, result?.reminderEnabled)
    }

    @Test
    fun getAllReturnsOrderedByName() = runBlocking {
        dao.insert(
            SubscriptionEntity(
                serviceName = "Spotify",
                price = 9.99,
                billingDay = 5,
                period = BillingPeriod.MONTHLY,
                reminderEnabled = false,
                notes = null
            )
        )
        dao.insert(
            SubscriptionEntity(
                serviceName = "Amazon Prime",
                price = 49.90,
                billingDay = 7,
                period = BillingPeriod.YEARLY,
                reminderEnabled = true,
                notes = null
            )
        )

        val all = dao.getAll().first()

        assertEquals(2, all.size)
        assertEquals("Amazon Prime", all[0].serviceName)
        assertEquals("Spotify", all[1].serviceName)
    }
}
