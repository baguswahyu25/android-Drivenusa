            package projectichif.DriveNusa.api

            import com.google.gson.annotations.SerializedName
            import okhttp3.MultipartBody
            import okhttp3.RequestBody
            import projectichif.DriveNusa.PromoItem
            import retrofit2.Response
            import retrofit2.http.*
            import okhttp3.ResponseBody
            import projectichif.DriveNusa.NotificationItem
            import projectichif.DriveNusa.api.AuthRepository


            // =============================
            // 1. Model Data User
            // =============================

            data class UserData(
                @SerializedName("id") val id: Int?,
                @SerializedName("name") val name: String?,
                @SerializedName("email") val email: String?,
                @SerializedName("profile_photo_path") val profilePhotoPath: String?,
                @SerializedName("profile_photo_url") val profilePhotoUrl: String? = null,
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

            data class UpdateProfileResponse(
                @SerializedName("status") val status: Boolean,
                @SerializedName("message") val message: String,
                @SerializedName("data") val data: UpdateProfileData?
            )

            data class UpdateProfileData(
                @SerializedName("user") val user: UserData
            )

            data class ChatMessage(
                val id: Int,
                val roomId: Int,
                val senderId: Int,
                val message: String,
                val createdAt: String
            )
            data class Message(
                val text: String,
                val time: String,
                val isSentByUser: Boolean,
                val avatar: Int? = null
            )



            data class FormSubmitResponse(
                val success: Boolean,
                val message: String?,
                val data: Any? = null
            )



            data class BotRequest(
                val message: String
            )

            // ================= BOT RESPONSE =================
            data class BotResponse(
                val success: Boolean,
                val reply: String,
                val source: String,
                val chat_id: Int? = null
            )

            // =============================
            // 2. AUTH RESPONSE
            // =============================
            data class AuthResponse(
                @SerializedName("success") val success: Boolean? = null,
                @SerializedName("status") val status: Boolean? = null,
                @SerializedName("message") val message: String? = null,
                @SerializedName("token") val token: String? = null,

                @SerializedName("user") val user: UserData? = null,
                @SerializedName("data") val data: UserData? = null,

                // fallback
                @SerializedName("id") val id: Int? = null,
                @SerializedName("name") val name: String? = null,
                @SerializedName("email") val email: String? = null,
                @SerializedName("profile_photo_path") val fallbackProfilePath: String? = null,
                @SerializedName("profile_photo_url") val fallbackProfileUrl: String? = null,

                ) {

                fun isSuccess(): Boolean {
                    return (success == true || status == true || !token.isNullOrEmpty())
                }

                fun getUserModel(): UserData? {
                    val u = user ?: data
                    if (u != null) {
                        val safeUrl = u.profilePhotoUrl?.let { url ->
                            if (url.startsWith("http")) url
                            else "${AuthRepository.BASE_IMAGE_URL}/$url"
                        }
                        return u.copy(profilePhotoUrl = safeUrl)
                    }
                    return null
                }



            }

            // =============================
            // 3. AUTH API
            // =============================
            interface AuthApi {

                // ===== AUTH =====
                @POST("login")
                suspend fun loginUser(@Body data: Map<String, String>): Response<AuthResponse>

                @POST("register")
                suspend fun registerUser(@Body data: Map<String, String>): Response<AuthResponse>

                @GET("user")
                suspend fun getUser(@Header("Authorization") token: String): Response<AuthResponse>

                @Multipart
                @POST("user/update")
                suspend fun updateProfile(
                    @Header("Authorization") token: String,
                    @Part("name") name: RequestBody,
                    @Part profile_photo: MultipartBody.Part?
                ): Response<UpdateProfileResponse>


            @POST("user/change-password")
            suspend fun changePassword(
                @Header("Authorization") token: String,
                @Body data: Map<String, String>
            ): Response<AuthResponse>

            @POST("email/resend")
            suspend fun sendVerificationEmail(@Header("Authorization") token: String): Response<AuthResponse>

            @POST("forgot-password")
            suspend fun forgotPassword(@Body data: Map<String, String>): Response<AuthResponse>

            // ===== FORM =====
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
            ): Response<FormSubmitResponse>

            // ===== BOT =====
            @POST("bot/chat")
            suspend fun sendBotMessage(
                @Header("Authorization") token: String,
                @Body request: BotRequest
            ): Response<BotResponse>

            // ===== NOTIF =====
            @POST("user/notification-preference")
            suspend fun updateNotifPreference(
                @Header("Authorization") token: String,
                @Body body: Map<String, Boolean>
            ): Response<AuthResponse>

            // ===== PROMO =====
            @GET("promos")
            suspend fun getPromos(): Response<List<PromoItem>>

            @GET("promos/{id}")
            suspend fun getPromoDetail(@Path("id") id: Int): Response<PromoItem>

            // ===== CHAT =====
            @POST("chat/send")
            suspend fun sendChat(
                @Header("Authorization") token: String,
                @Body body: Map<String, Any>
            ): Response<Unit>

            @GET("chat/{roomId}")
            suspend fun getChats(
                @Header("Authorization") token: String,
                @Path("roomId") roomId: Int
            ): Response<List<ChatMessage>>
        }
