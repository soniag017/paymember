package com.paymember.data.remote

class RemoteAuthManager(
    private val apiService: ApiService,
    private val sessionStore: SessionStore
) {
    fun isLoggedIn(): Boolean = !sessionStore.getToken().isNullOrBlank()

    fun currentDisplayName(): String? = sessionStore.getUserEmail()?.toDisplayName()

    suspend fun login(email: String, password: String) {
        val auth = apiService.login(AuthRequest(email.trim(), password))
        sessionStore.saveToken(auth.token)
        sessionStore.saveUserEmail(auth.email)
    }

    suspend fun register(email: String, password: String) {
        val auth = apiService.register(AuthRequest(email.trim(), password))
        sessionStore.saveToken(auth.token)
        sessionStore.saveUserEmail(auth.email)
    }

    suspend fun loginWithGoogle(idToken: String) {
        val auth = apiService.loginWithGoogle(GoogleAuthRequest(idToken))
        sessionStore.saveToken(auth.token)
        sessionStore.saveUserEmail(auth.email)
    }

    fun logout() {
        sessionStore.clearToken()
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
