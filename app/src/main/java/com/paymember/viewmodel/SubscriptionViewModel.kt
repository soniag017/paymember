package com.paymember.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import com.paymember.data.repository.SubscriptionRepository
import com.paymember.data.repository.SyncUiState
import java.time.LocalDate
import java.time.format.DateTimeParseException
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
    val reminderDaysBefore: Int = 0,
    val startDate: String = LocalDate.now().toString(),
    val customIconUri: String = "",
    val notes: String = ""
)

class SubscriptionViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {

    val subscriptions = repository.getAllSubscriptions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val syncState = repository.getSyncState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SyncUiState())

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
                reminderDaysBefore = item.reminderDaysBefore,
                startDate = item.startDate.ifBlank { LocalDate.now().toString() },
                customIconUri = item.customIconUri,
                notes = item.notes.orEmpty()
            )
        }
    }

    fun saveSubscription(
        onError: (String) -> Unit = {},
        onDone: (SubscriptionEntity) -> Unit = {}
    ) {
        val current = _formState.value
        val normalizedPrice = current.price.replace(',', '.')
        val price = normalizedPrice.toDoubleOrNull() ?: return
        val billingDay = current.billingDay.toIntOrNull() ?: return
        val startDate = current.startDate.toLocalDateOrNull() ?: return
        if (current.serviceName.isBlank() || billingDay !in 1..31 || price <= 0.0) return

        val entity = SubscriptionEntity(
            id = current.id,
            serviceName = current.serviceName.trim(),
            price = price,
            billingDay = billingDay,
            period = current.period,
            reminderEnabled = current.reminderEnabled,
            reminderDaysBefore = current.reminderDaysBefore.coerceIn(0, 30),
            notes = current.notes.ifBlank { null },
            startDate = startDate.toString(),
            customIconUri = current.customIconUri
        )

        viewModelScope.launch {
            runCatching {
                if (entity.id == 0) {
                    val generatedId = repository.addSubscription(entity).toInt()
                    entity.copy(id = generatedId)
                } else {
                    repository.updateSubscription(entity)
                    entity
                }
            }.onSuccess { savedEntity ->
                clearForm()
                onDone(savedEntity)
            }.onFailure { ex ->
                onError(ex.friendlyMessage("No se pudo guardar la suscripción."))
            }
        }
    }

    fun deleteSubscription(
        subscription: SubscriptionEntity,
        onDone: (Int) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            runCatching {
                repository.deleteSubscription(subscription)
            }.onSuccess {
                onDone(subscription.id)
            }.onFailure { ex ->
                onError(ex.friendlyMessage("No se pudo eliminar la suscripción."))
            }
        }
    }

    fun refreshSubscriptions(
        onDone: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            runCatching {
                repository.refreshIfLoggedIn()
            }.onSuccess { refreshed ->
                if (refreshed) onDone()
            }.onFailure { ex ->
                onError(ex.friendlyMessage("No se pudo actualizar."))
            }
        }
    }

    fun clearForm() {
        _formState.value = SubscriptionFormState()
    }

    fun isFormValid(): Boolean {
        val current = _formState.value
        val normalizedPrice = current.price.replace(',', '.')
        val price = normalizedPrice.toDoubleOrNull() ?: return false
        val billingDay = current.billingDay.toIntOrNull() ?: return false
        val startDate = current.startDate.toLocalDateOrNull() ?: return false
        return current.serviceName.isNotBlank() &&
            billingDay in 1..31 &&
            price > 0.0 &&
            !startDate.isAfter(LocalDate.now())
    }

    private fun String.toLocalDateOrNull(): LocalDate? {
        return try {
            LocalDate.parse(trim())
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private fun Throwable.friendlyMessage(fallback: String): String {
        return message?.takeIf { it.isNotBlank() } ?: fallback
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
