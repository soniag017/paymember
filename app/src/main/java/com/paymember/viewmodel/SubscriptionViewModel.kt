package com.paymember.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import com.paymember.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SubscriptionFormState(
    val id: Int = 0,
    val serviceName: String = "",
    val price: String = "",
    val billingDay: String = "",
    val period: BillingPeriod = BillingPeriod.MONTHLY,
    val reminderEnabled: Boolean = false,
    val notes: String = ""
)

class SubscriptionViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {

    val subscriptions = repository.getAllSubscriptions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _formState = MutableStateFlow(SubscriptionFormState())
    val formState: StateFlow<SubscriptionFormState> = _formState.asStateFlow()

    fun updateForm(update: SubscriptionFormState.() -> SubscriptionFormState) {
        _formState.value = _formState.value.update()
    }

    fun loadSubscription(id: Int) {
        if (id == 0) {
            clearForm()
            return
        }

        viewModelScope.launch {
            val item = repository.getSubscriptionById(id) ?: return@launch
            _formState.value = SubscriptionFormState(
                id = item.id,
                serviceName = item.serviceName,
                price = item.price.toString(),
                billingDay = item.billingDay.toString(),
                period = item.period,
                reminderEnabled = item.reminderEnabled,
                notes = item.notes.orEmpty()
            )
        }
    }

    fun saveSubscription(onDone: () -> Unit) {
        val current = _formState.value
        val price = current.price.toDoubleOrNull() ?: return
        val billingDay = current.billingDay.toIntOrNull() ?: return
        if (current.serviceName.isBlank() || billingDay !in 1..31) return

        val entity = SubscriptionEntity(
            id = current.id,
            serviceName = current.serviceName.trim(),
            price = price,
            billingDay = billingDay,
            period = current.period,
            reminderEnabled = current.reminderEnabled,
            notes = current.notes.ifBlank { null }
        )

        viewModelScope.launch {
            if (entity.id == 0) {
                repository.addSubscription(entity)
            } else {
                repository.updateSubscription(entity)
            }
            clearForm()
            onDone()
        }
    }

    fun deleteSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }

    fun clearForm() {
        _formState.value = SubscriptionFormState()
    }
}

class SubscriptionViewModelFactory(
    private val repository: SubscriptionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubscriptionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
