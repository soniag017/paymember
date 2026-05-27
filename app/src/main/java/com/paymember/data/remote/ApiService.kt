package com.paymember.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @Multipart
    @POST("api/subscriptions/{id}/icon")
    suspend fun uploadSubscriptionIcon(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): SubscriptionPayload

    @GET("api/subscriptions/{id}/icon")
    suspend fun getSubscriptionIcon(@Path("id") id: Long): ResponseBody

    @DELETE("api/subscriptions/{id}/icon")
    suspend fun deleteSubscriptionIcon(@Path("id") id: Long): SubscriptionPayload
}
