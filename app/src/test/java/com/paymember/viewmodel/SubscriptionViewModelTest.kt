package com.paymember.viewmodel

import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import com.paymember.data.repository.SubscriptionRepository
import com.paymember.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: SubscriptionRepository = mock()

    @Test
    fun saveSubscription_validForm_callsRepository() = runTest {
        whenever(repository.getAllSubscriptions()).thenReturn(flowOf(emptyList()))
        whenever(repository.addSubscription(any())).thenReturn(1L)

        val viewModel = SubscriptionViewModel(repository)

        viewModel.updateForm {
            copy(
                serviceName = "Disney+",
                price = "8.99",
                billingDay = "15",
                period = BillingPeriod.MONTHLY,
                reminderEnabled = true,
                notes = "Perfil familiar"
            )
        }

        var callbackCalled = false
        viewModel.saveSubscription {
            callbackCalled = true
        }

        advanceUntilIdle()

        verify(repository).addSubscription(any())
        assertTrue(callbackCalled)
    }

    @Test
    fun saveSubscription_invalidForm_doesNotCallRepository() = runTest {
        whenever(repository.getAllSubscriptions()).thenReturn(flowOf(emptyList()))

        val viewModel = SubscriptionViewModel(repository)

        viewModel.updateForm {
            copy(
                serviceName = "",
                price = "abc",
                billingDay = "99",
                period = BillingPeriod.MONTHLY,
                reminderEnabled = false,
                notes = ""
            )
        }

        viewModel.saveSubscription { }

        advanceUntilIdle()

        verify(repository, never()).addSubscription(any())
        assertFalse(viewModel.isFormValid())
    }

    @Test
    fun isFormValid_acceptsCommaDecimalPrice() = runTest {
        whenever(repository.getAllSubscriptions()).thenReturn(flowOf(emptyList()))
        val viewModel = SubscriptionViewModel(repository)

        viewModel.updateForm {
            copy(
                serviceName = "HBO Max",
                price = "10,50",
                billingDay = "12",
                period = BillingPeriod.MONTHLY
            )
        }

        assertTrue(viewModel.isFormValid())
    }
}
