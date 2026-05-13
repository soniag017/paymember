package com.paymember.data.repository

import com.paymember.data.model.SubscriptionEntity
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
    private val authManager: RemoteAuthManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val items = MutableStateFlow<List<SubscriptionEntity>>(emptyList())

    init {
        if (authManager.isLoggedIn()) {
            scope.launch { refresh() }
        }
    }

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> = items.asStateFlow()

    suspend fun getSubscriptionById(id: Int): SubscriptionEntity? {
        requireSession()
        return apiService.getSubscription(id.toLong()).toEntity()
    }

    suspend fun addSubscription(subscription: SubscriptionEntity): Long {
        requireSession()
        val saved = apiService.createSubscription(subscription.toPayload()).toEntity()
        refresh()
        return saved.id.toLong()
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        requireSession()
        apiService.updateSubscription(subscription.id.toLong(), subscription.toPayload())
        refresh()
    }

    suspend fun deleteSubscription(subscription: SubscriptionEntity) {
        requireSession()
        apiService.deleteSubscription(subscription.id.toLong())
        refresh()
    }

    suspend fun refreshIfLoggedIn() {
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
}
