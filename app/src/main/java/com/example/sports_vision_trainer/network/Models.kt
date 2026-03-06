package com.example.sports_vision_trainer.network

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ForgotRequest(
    val email:String
)

data class OtpVerifyRequest(
    val email:String,
    val otp:String
)

data class ResetRequest(
    val email:String,
    val password:String
)

data class ApiResponse(
    val status:String,
    val message:String? = null,
    val msg:String? = null,
    val username:String? = null
)

data class ProfileResponse(
    val username: String?,
    val bio: String?,
    val profile_image: String?
)


