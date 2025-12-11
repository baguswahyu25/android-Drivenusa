package projectichif.DriveNusa.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

// =============================
// 1. Model Data User
// =============================
data class UserData(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("email_verified_at") val emailVerifiedAt: String? = null,
)
data class FormRequest(
    val paket: String,
    val nama_lengkap: String,
    val tempat_lahir: String,
    val tanggal_lahir: String,
    val alamat: String,
    val jenis_kelamin: String,
    val pekerjaan: String,
    val mobil_dipilih: String, // 🔥 perbaikan
    val metode_pembayaran: String,
    val opsi_kredit: String? = null,
    val harga: Int,
    val tipe_pendaftaran: String = "non_sim" // 🔥 default sesuai migration
)

data class FormSubmitResponse(
    val success: Boolean,
    val message: String?,
    val data: Any? = null
)
// =============================
// 2. Model Response Laravel
// =============================
data class AuthResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("status") val status: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("token") val token: String? = null,

    @SerializedName("user") val user: UserData? = null,
    @SerializedName("data") val data: UserData? = null,

    // fallback respons sederhana
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
) {
    fun isSuccess(): Boolean {
        return (success == true || status == true || !token.isNullOrEmpty())
    }

    fun getUserModel(): UserData? {
        return when {
            user != null -> user
            data != null -> data
            id != null && name != null -> UserData(id, name, email)
            else -> null
        }
    }
}


// =============================
// 3. AUTH API
// =============================
interface AuthApi {

    @POST("login")
    suspend fun loginUser(@Body data: Map<String, String>): Response<AuthResponse>

    @POST("register")
    suspend fun registerUser(@Body data: Map<String, String>): Response<AuthResponse>

    // NOTE: endpoint ini butuh Authorization header (sesuai backend kamu)
    @POST("email/resend")
    suspend fun sendVerificationEmail(
        @Header("Authorization") token: String
    ): Response<AuthResponse>


    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): Response<AuthResponse>

    // ===========================
    //   UPDATE PROFILE + AVATAR
    // ===========================
    @Multipart
    @POST("user/update")
    suspend fun updateProfile(
        @Header("Authorization") auth: String,
        @Part("_method") method: RequestBody,
        @Part("name") name: RequestBody,
        @Part avatar: MultipartBody.Part?
    ): Response<AuthResponse>

    @POST("login/android/google")
    suspend fun loginWithGoogle(@Body data: Map<String, String>): Response<AuthResponse>
    @POST("forgot-password")
    suspend fun forgotPassword(@Body data: Map<String, String>): Response<AuthResponse>

    @Headers("Accept: application/json")
    @POST("v1/pendaftaran")
    suspend fun submitForm(
        @Header("Authorization") token: String,
        @Body data: FormRequest
    ): Response<FormSubmitResponse>

    @Multipart
    @POST("v1/pendaftaran")
    suspend fun submitFormSim(
        @Header("Authorization") token: String,
        @PartMap parts: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part pas_foto: MultipartBody.Part,
        @Part ktp: MultipartBody.Part
    ): Response<AuthResponse>

}

