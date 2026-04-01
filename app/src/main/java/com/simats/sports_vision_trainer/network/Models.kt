package com.simats.sports_vision_trainer.network

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
    val username:String? = null,
    val id: Int? = null,
    val app_status: String? = null
)

data class ProfileResponse(
    val username: String?,
    val bio: String?,
    val profile_image: String?
)

data class SessionSaveRequest(
    val email: String,
    val gameType: String,
    val score: Int,
    val avgReaction: Long,
    val wrong: Int,
    val timestamp: Long
)

data class SessionResponse(
    val status: String,
    val sessions: List<com.simats.sports_vision_trainer.model.GameSession>? = null
)

data class DoctorRegisterRequest(
    val full_name: String,
    val medical_license: String,
    val hospital_name: String,
    val clinic_email: String,
    val password: String
)

data class DoctorLoginRequest(
    val clinic_email: String,
    val password: String
)

data class Doctor(
    val full_name: String,
    val medical_license: String,
    val hospital_name: String,
    val clinic_email: String,
    val profile_image: String? = null
)

data class DoctorListResponse(
    val status: String,
    val doctors: List<Doctor>? = null
)

data class AppointmentRequest(
    val doctor_email: String,
    val athlete_email: String,
    val athlete_name: String,
    val athlete_phone: String,
    val date: String,
    val time: String
)

data class UpdateStatusRequest(
    val id: Int,
    val status: String
)

data class Appointment(
    val id: Int,
    val doctor_email: String,
    val athlete_email: String,
    val athlete_name: String,
    val athlete_phone: String,
    val date: String,
    val time: String,
    val status: String,
    val profile_image: String? = null,
    val doctor_name: String = "Doctor"
)

data class AppointmentListResponse(
    val status: String,
    val appointments: List<Appointment>? = null,
    val bookings: List<Appointment>? = null,
    val message: String? = null
)
