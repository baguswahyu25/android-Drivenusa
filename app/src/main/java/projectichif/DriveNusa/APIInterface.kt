package projectichif.DriveNusa.api

import projectichif.DriveNusa.api.AuthResponse
import retrofit2.Call
import retrofit2.http.*

// Request login
data class LoginRequest(
    val email: String,
    val password: String
)

// Request register
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

// Data user
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val emailConfirmedAt: String?,
    val token: String?
)

interface ApiService {

    @POST("login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>   // ← DIHIDUPKAN LAGI

    @GET("user")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<User>
}
