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

    @POST("save_session.php")
    fun saveSession(
        @Body req: SessionSaveRequest
    ): Call<ApiResponse>

    @GET("get_sessions.php")
    fun getSessions(
        @Query("email") email: String
    ): Call<SessionResponse>

    @GET("get_athlete_notifications.php")
    fun getAthleteNotifications(@Query("email") email: String): Call<ApiResponse>

    @GET("get_athlete_bookings.php")
    fun getAthleteBookings(@Query("email") email: String): Call<AppointmentListResponse>

    @POST("cancel_appointment.php")
    fun cancelAppointment(@Query("id") id: Int): Call<ApiResponse>

    @GET("get_accepted_appointments.php")
    fun getAcceptedAppointments(@Query("email") email: String): Call<AppointmentListResponse>

    @GET("get_doctor_history.php")
    fun getDoctorHistory(@Query("email") email: String): Call<AppointmentListResponse>

    @POST("book_appointment.php")
    fun bookAppointment(@Body req: AppointmentRequest): Call<ApiResponse>

    @GET("get_doctor_appointments.php")
    fun getDoctorAppointments(@Query("email") email: String): Call<AppointmentListResponse>

    @POST("update_appointment_status.php")
    fun updateAppointmentStatus(@Body req: UpdateStatusRequest): Call<ApiResponse>

    @POST("doctor_register.php")
    fun doctorRegister(
        @Body req: DoctorRegisterRequest
    ): Call<ApiResponse>

    @POST("doctor_login.php")
    fun doctorLogin(@Body req: DoctorLoginRequest): Call<ApiResponse>

    @POST("doctor_send_otp.php")
    fun doctorSendOtp(@Body req: ForgotRequest): Call<ApiResponse>

    @POST("doctor_verify_otp.php")
    fun doctorVerifyOtp(@Body req: OtpVerifyRequest): Call<ApiResponse>

    @POST("doctor_reset_password_final.php")
    fun doctorResetPasswordFinal(@Body req: ResetRequest): Call<ApiResponse>

    @GET("get_doctors.php")
    fun getDoctors(): Call<DoctorListResponse>

    @GET("get_doctor_profile.php")
    fun getDoctorProfile(
        @Query("email") email: String
    ): Call<ProfileResponse>

    @Multipart
    @POST("save_doctor_profile.php")
    fun saveDoctorProfile(
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("bio") bio: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Call<ApiResponse>
}