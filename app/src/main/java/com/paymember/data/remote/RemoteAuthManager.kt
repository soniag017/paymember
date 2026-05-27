package com.paymember.data.remote

import org.json.JSONObject
import retrofit2.HttpException

class RemoteAuthManager(
    private val apiService: ApiService,
    private val sessionStore: SessionStore
) {
    fun isLoggedIn(): Boolean = !sessionStore.getToken().isNullOrBlank()

    fun currentDisplayName(): String? = sessionStore.getUserEmail()?.toDisplayName()

    suspend fun login(email: String, password: String) {
        val auth = runApiCall { apiService.login(AuthRequest(email.trim(), password)) }
        sessionStore.saveToken(auth.token)
        sessionStore.saveUserEmail(auth.email)
    }

    suspend fun register(email: String, password: String) {
        val auth = runApiCall { apiService.register(AuthRequest(email.trim(), password)) }
        sessionStore.saveToken(auth.token)
        sessionStore.saveUserEmail(auth.email)
    }

    suspend fun loginWithGoogle(idToken: String) {
        val auth = runApiCall { apiService.loginWithGoogle(GoogleAuthRequest(idToken)) }
        sessionStore.saveToken(auth.token)
        sessionStore.saveUserEmail(auth.email)
    }

    fun logout() {
        sessionStore.clearToken()
    }

    private suspend fun <T> runApiCall(call: suspend () -> T): T {
        try {
            return call()
        } catch (ex: HttpException) {
            throw IllegalStateException(ex.readApiError() ?: "Error HTTP ${ex.code()}")
        }
    }

    private fun HttpException.readApiError(): String? {
        val rawBody = response()?.errorBody()?.string().orEmpty()
        if (rawBody.isBlank()) return null

        return runCatching {
            JSONObject(rawBody).optString("error").takeIf { it.isNotBlank() }
        }.getOrNull() ?: rawBody.take(160)
    }

    private fun String.toDisplayName(): String {
        return substringBefore("@")
            .replace('.', ' ')
            .replace('_', ' ')
            .replace('-', ' ')
            .trim()
            .ifBlank { "Invitada" }
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase() else char.toString()
                }
            }
    }
}
