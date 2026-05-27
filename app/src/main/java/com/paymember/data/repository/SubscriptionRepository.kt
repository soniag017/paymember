package com.paymember.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.paymember.data.model.SubscriptionEntity
import com.paymember.data.model.BillingPeriod
import com.paymember.data.remote.ApiService
import com.paymember.data.remote.RemoteAuthManager
import com.paymember.data.remote.SubscriptionPayload
import com.paymember.data.remote.toPayload
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class SyncUiState(
    val isRefreshing: Boolean = false,
    val lastSyncedAt: LocalDateTime? = null,
    val errorMessage: String? = null
)

class SubscriptionRepository(
    private val apiService: ApiService,
    private val authManager: RemoteAuthManager,
    private val appContext: Context? = null,
    demoMode: Boolean = false
) {
    private companion object {
        const val ICON_MAX_SIDE = 512
        const val ICON_MAX_BYTES = 1_500_000
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isDemoMode = demoMode
    private var nextDemoId = 100
    private val items = MutableStateFlow(if (isDemoMode) demoSubscriptions() else emptyList())
    private val syncState = MutableStateFlow(
        SyncUiState(lastSyncedAt = if (isDemoMode) LocalDateTime.now() else null)
    )

    init {
        if (!isDemoMode && authManager.isLoggedIn()) {
            scope.launch { runCatching { refresh() } }
        }
    }

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> = items.asStateFlow()

    fun getSyncState(): Flow<SyncUiState> = syncState.asStateFlow()

    fun enableDemoMode() {
        isDemoMode = true
        nextDemoId = 100
        items.value = demoSubscriptions()
        markSynced()
    }

    fun clearForLogout() {
        isDemoMode = false
        nextDemoId = 100
        items.value = emptyList()
        syncState.value = SyncUiState()
    }

    suspend fun getSubscriptionById(id: Int): SubscriptionEntity? {
        if (isDemoMode) return items.value.firstOrNull { it.id == id }
        requireSession()
        return apiService.getSubscription(id.toLong()).toEntityWithCachedIcon()
    }

    suspend fun addSubscription(subscription: SubscriptionEntity): Long {
        if (isDemoMode) {
            val generatedId = nextDemoId++
            items.value = items.value + subscription.copy(id = generatedId)
            markSynced()
            return generatedId.toLong()
        }
        requireSession()
        return withSyncStatus {
            val customIconUri = subscription.customIconUri
            val saved = apiService.createSubscription(subscription.copy(customIconUri = "").toPayload()).toEntity()
            uploadIconIfNeeded(saved.id.toLong(), customIconUri)
            loadRemoteItems()
            saved.id.toLong()
        }
    }

    suspend fun updateSubscription(subscription: SubscriptionEntity) {
        if (isDemoMode) {
            items.value = items.value.map { item ->
                if (item.id == subscription.id) subscription else item
            }
            markSynced()
            return
        }
        requireSession()
        withSyncStatus {
            val customIconUri = subscription.customIconUri
            apiService.updateSubscription(subscription.id.toLong(), subscription.copy(customIconUri = "").toPayload())
            syncRemoteIcon(subscription.id.toLong(), customIconUri)
            loadRemoteItems()
        }
    }

    suspend fun deleteSubscription(subscription: SubscriptionEntity) {
        if (isDemoMode) {
            items.value = items.value.filterNot { it.id == subscription.id }
            markSynced()
            return
        }
        requireSession()
        withSyncStatus {
            apiService.deleteSubscription(subscription.id.toLong())
            deleteCachedIcon(subscription.id.toLong())
            loadRemoteItems()
        }
    }

    suspend fun refreshIfLoggedIn(): Boolean {
        if (isDemoMode) {
            markSynced()
            return true
        }
        if (!authManager.isLoggedIn()) {
            syncState.value = syncState.value.copy(
                isRefreshing = false,
                errorMessage = "Sin sesión activa"
            )
            return false
        }
        refresh()
        return true
    }

    private suspend fun refresh() {
        withSyncStatus {
            loadRemoteItems()
        }
    }

    private suspend fun loadRemoteItems() {
        items.value = apiService.getSubscriptions().map { it.toEntityWithCachedIcon() }
    }

    private suspend fun <T> withSyncStatus(block: suspend () -> T): T {
        syncState.value = syncState.value.copy(isRefreshing = true, errorMessage = null)
        return try {
            val result = block()
            syncState.value = SyncUiState(
                isRefreshing = false,
                lastSyncedAt = LocalDateTime.now(),
                errorMessage = null
            )
            result
        } catch (ex: Exception) {
            syncState.value = syncState.value.copy(
                isRefreshing = false,
                errorMessage = ex.friendlySyncMessage()
            )
            throw ex
        }
    }

    private fun markSynced() {
        syncState.value = SyncUiState(
            isRefreshing = false,
            lastSyncedAt = LocalDateTime.now(),
            errorMessage = null
        )
    }

    private fun Throwable.friendlySyncMessage(): String {
        return message?.takeIf { it.isNotBlank() } ?: "No se pudo sincronizar"
    }

    private fun requireSession() {
        if (!authManager.isLoggedIn()) {
            throw IllegalStateException("User not authenticated")
        }
    }

    private suspend fun SubscriptionPayload.toEntityWithCachedIcon(): SubscriptionEntity {
        val entity = toEntity()
        val subscriptionId = id ?: return entity
        if (customIconUri.isNullOrBlank()) return entity.copy(customIconUri = "")
        val cachedUri = cacheRemoteIcon(subscriptionId)
        return if (cachedUri.isNullOrBlank()) entity else entity.copy(customIconUri = cachedUri)
    }

    private suspend fun syncRemoteIcon(subscriptionId: Long, customIconUri: String) {
        if (customIconUri.isBlank()) {
            apiService.deleteSubscriptionIcon(subscriptionId)
            deleteCachedIcon(subscriptionId)
            return
        }
        uploadIconIfNeeded(subscriptionId, customIconUri)
    }

    private suspend fun uploadIconIfNeeded(subscriptionId: Long, customIconUri: String) {
        if (!customIconUri.isUploadableIconUri()) return
        val part = buildIconPart(subscriptionId, customIconUri) ?: return
        apiService.uploadSubscriptionIcon(subscriptionId, part)
    }

    private fun String.isUploadableIconUri(): Boolean {
        return startsWith("content:") || startsWith("file:")
    }

    private fun buildIconPart(subscriptionId: Long, customIconUri: String): MultipartBody.Part? {
        val bytes = decodeAndCompressIcon(customIconUri) ?: return null
        val body = bytes.toRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData(
            name = "file",
            filename = "subscription-$subscriptionId.jpg",
            body = body
        )
    }

    private fun decodeAndCompressIcon(customIconUri: String): ByteArray? {
        val context = appContext ?: return null
        val sourceBytes = runCatching {
            context.contentResolver.openInputStream(Uri.parse(customIconUri))?.use { it.readBytes() }
        }.getOrNull() ?: return null

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(sourceBytes, 0, sourceBytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            return sourceBytes.takeIf { it.size <= ICON_MAX_BYTES }
        }

        var sampleSize = 1
        while (bounds.outWidth / sampleSize > ICON_MAX_SIDE || bounds.outHeight / sampleSize > ICON_MAX_SIDE) {
            sampleSize *= 2
        }

        val bitmap = BitmapFactory.decodeByteArray(
            sourceBytes,
            0,
            sourceBytes.size,
            BitmapFactory.Options().apply { inSampleSize = sampleSize }
        ) ?: return sourceBytes.takeIf { it.size <= ICON_MAX_BYTES }

        return try {
            val out = ByteArrayOutputStream()
            var quality = 88
            var result: ByteArray
            do {
                out.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                result = out.toByteArray()
                quality -= 10
            } while (result.size > ICON_MAX_BYTES && quality >= 48)
            result.takeIf { it.size <= ICON_MAX_BYTES }
        } finally {
            bitmap.recycle()
        }
    }

    private suspend fun cacheRemoteIcon(subscriptionId: Long): String? {
        val context = appContext ?: return null
        val dir = File(context.cacheDir, "paymember-subscription-icons").apply { mkdirs() }
        val fallback = latestCachedIcon(subscriptionId, dir)

        return runCatching {
            val bytes = apiService.getSubscriptionIcon(subscriptionId).bytes()
            dir.listFiles { _, name -> name.startsWith("subscription-$subscriptionId-") }
                ?.forEach { it.delete() }
            val file = File(dir, "subscription-$subscriptionId-${System.currentTimeMillis()}.img")
            file.writeBytes(bytes)
            Uri.fromFile(file).toString()
        }.getOrElse {
            fallback?.let { Uri.fromFile(it).toString() }
        }
    }

    private fun latestCachedIcon(subscriptionId: Long, dir: File): File? {
        return dir.listFiles { _, name -> name.startsWith("subscription-$subscriptionId-") }
            ?.maxByOrNull { it.lastModified() }
    }

    private fun deleteCachedIcon(subscriptionId: Long) {
        val dir = File(appContext?.cacheDir ?: return, "paymember-subscription-icons")
        dir.listFiles { _, name -> name.startsWith("subscription-$subscriptionId-") }
            ?.forEach { it.delete() }
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
