package com.example.sports_vision_trainer.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("register.php")
    fun register(
        @Body req: RegisterRequest
    ): Call<ApiResponse>

    @POST("login.php")
    fun login(
        @Body req: LoginRequest
    ): Call<ApiResponse>

    @POST("send_otp.php")
    fun sendOtp(
        @Body req: ForgotRequest
    ): Call<ApiResponse>

    @POST("verify_otp.php")
    fun verifyOtp(
        @Body req: OtpVerifyRequest
    ): Call<ApiResponse>

    @POST("reset_password_final.php")
    fun resetPasswordFinal(
        @Body req: ResetRequest
    ): Call<ApiResponse>

    // ✅ PROFILE SAVE — FIXED ORDER (ONLY CHANGE)
    @Multipart
    @POST("save_profile.php")
    fun saveProfile(
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("bio") bio: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Call<ApiResponse>

    @GET("get_profile.php")
    fun getProfile(
        @Query("email") email: String
    ): Call<ProfileResponse>
}