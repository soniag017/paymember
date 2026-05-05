package com.paymember.data.repository

import com.paymember.data.dao.SubscriptionDao
import com.paymember.data.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

class SubscriptionRepository(private val dao: SubscriptionDao) {
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> = dao.getAll()

    suspend fun getSubscriptionById(id: Int): SubscriptionEntity? = dao.getById(id)

    suspend fun addSubscription(subscription: SubscriptionEntity): Long = dao.insert(subscription)

    suspend fun updateSubscription(subscription: SubscriptionEntity) = dao.update(subscription)

    suspend fun deleteSubscription(subscription: SubscriptionEntity) = dao.delete(subscription)
}
