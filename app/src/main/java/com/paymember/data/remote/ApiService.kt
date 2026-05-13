package com.paymember.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): AuthResponse

    @GET("api/subscriptions")
    suspend fun getSubscriptions(): List<SubscriptionPayload>

    @GET("api/subscriptions/{id}")
    suspend fun getSubscription(@Path("id") id: Long): SubscriptionPayload

    @POST("api/subscriptions")
    suspend fun createSubscription(@Body payload: SubscriptionPayload): SubscriptionPayload

    @PUT("api/subscriptions/{id}")
    suspend fun updateSubscription(@Path("id") id: Long, @Body payload: SubscriptionPayload): SubscriptionPayload

    @DELETE("api/subscriptions/{id}")
    suspend fun deleteSubscription(@Path("id") id: Long)
}
