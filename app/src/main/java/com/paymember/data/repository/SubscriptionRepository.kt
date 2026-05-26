package com.paymember.data.repository

import com.paymember.data.model.SubscriptionEntity
import com.paymember.data.model.BillingPeriod
import com.paymember.data.remote.ApiService
import com.paymember.data.remote.RemoteAuthManager
import com.paymember.data.remote.toPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubscriptionRepository(
    private val apiService: ApiService,
    private val authManager: RemoteAuthManager,
    demoMode: Boolean = false
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isDemoMode = demoMode
    private var nextDemoId = 100
    private val items = MutableStateFlow(if (isDemoMode) demoSubscriptions() else emptyList())

    init {
        if (!isDemoMode && authManager.isLoggedIn()) {
            scope.launch { refresh() }
        }
    }

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> = items.asStateFlow()

    fun enableDemoMode() {
        isDemoMode = true
        nextDemoId = 100
        items.value = demoSubscriptions()
    }

    fun clearForLogout() {
        isDemoMode = false
        nextDemoId = 100
        items.value = emptyList()
    }

    suspend fun getSubscriptionById(id: Int): SubscriptionEntity? {
        if (isDemoMode) return items.value.firstOrNull { it.id == id }
        requireSession()
        return apiService.getSubscription(id.toLong()).toEntity()
    }

    suspend fun addSubscription(subscription: SubscriptionEntity): Long {
        if (isDemoMode) {
            val generatedId = nextDemoId++
            items.value = items.value + subscription.copy(id = generatedId)
            return generatedId.toLong()
        }
        requireSession()
        val saved = apiService.createSubscription(subscription.toPayload()).toEntity()
        refresh()
        return saved.id.toLong()
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        if (isDemoMode) {
            items.value = items.value.map { item ->
                if (item.id == subscription.id) subscription else item
            }
            return
        }
        requireSession()
        apiService.updateSubscription(subscription.id.toLong(), subscription.toPayload())
        refresh()
    }

    suspend fun deleteSubscription(subscription: SubscriptionEntity) {
        if (isDemoMode) {
            items.value = items.value.filterNot { it.id == subscription.id }
            return
        }
        requireSession()
        apiService.deleteSubscription(subscription.id.toLong())
        refresh()
    }

    suspend fun refreshIfLoggedIn() {
        if (isDemoMode) return
        if (!authManager.isLoggedIn()) return
        refresh()
    }

    private suspend fun refresh() {
        items.value = apiService.getSubscriptions().map { it.toEntity() }
    }

    private fun requireSession() {
        if (!authManager.isLoggedIn()) {
            throw IllegalStateException("User not authenticated")
        }
    }

    private fun demoSubscriptions(): List<SubscriptionEntity> = listOf(
        SubscriptionEntity(
            id = 1,
            serviceName = "Netflix - Estandar",
            price = 13.99,
            billingDay = 5,
            period = BillingPeriod.MONTHLY,
            reminderEnabled = true,
            reminderDaysBefore = 1,
            notes = "Demo local sin backend",
            startDate = "2024-01-05"
        ),
        SubscriptionEntity(
            id = 2,
            serviceName = "Spotify - Duo",
            price = 16.99,
            billingDay = 12,
            period = BillingPeriod.MONTHLY,
            reminderEnabled = true,
            reminderDaysBefore = 3,
            notes = null,
            startDate = "2023-09-12"
        ),
        SubscriptionEntity(
            id = 3,
            serviceName = "Disney+ - Premium",
            price = 159.90,
            billingDay = 20,
            period = BillingPeriod.YEARLY,
            reminderEnabled = true,
            reminderDaysBefore = 7,
            notes = "Pago anual",
            startDate = "2022-05-20"
        )
    )
}
