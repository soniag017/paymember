package com.paymember.data.remote

class RemoteAuthManager(
    private val apiService: ApiService,
    private val sessionStore: SessionStore
) {
    fun isLoggedIn(): Boolean = !sessionStore.getToken().isNullOrBlank()

    suspend fun login(email: String, password: String) {
        val auth = apiService.login(AuthRequest(email.trim(), password))
        sessionStore.saveToken(auth.token)
    }

    suspend fun register(email: String, password: String) {
        val auth = apiService.register(AuthRequest(email.trim(), password))
        sessionStore.saveToken(auth.token)
    }

    suspend fun loginWithGoogle(idToken: String) {
        val auth = apiService.loginWithGoogle(GoogleAuthRequest(idToken))
        sessionStore.saveToken(auth.token)
    }

    fun logout() {
        sessionStore.clearToken()
    }
}
