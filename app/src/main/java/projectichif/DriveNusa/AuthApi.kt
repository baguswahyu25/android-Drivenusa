            package projectichif.DriveNusa.api

            import com.google.gson.annotations.SerializedName
            import okhttp3.Interceptor
            import okhttp3.MultipartBody
            import okhttp3.RequestBody
            import projectichif.DriveNusa.PromoItem
            import retrofit2.Response
            import retrofit2.http.*
            import okhttp3.ResponseBody
            import projectichif.DriveNusa.ApiClient
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
            data class PendaftaranAktifResponse(
                val id: Int,

                @SerializedName("status_pendaftaran")
                val statusPendaftaran: String? = null,

                @SerializedName("sisa_pertemuan")
                val sisaPertemuan: Int? = null,

                @SerializedName("total_pertemuan")
                val totalPertemuan: Int? = null,

                val transaction: TransactionData? = null
            )

            data class TransactionData(
                val transaction_status: String?
            )
            data class CicilanItem(
                val id: Int,
                val label: String,
                val amount: Int,
                val status: String,
                val tanggal: String?
            )
            data class RiwayatCicilanResponse(
                val status_pembayaran: String,
                val items: List<CicilanItem>
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
                val tipe_pendaftaran: String = "non_sim" // 🔥 default sesuai migration
            )

            data class UpdateProfileResponse(
                @SerializedName("success") val success: Boolean,
                @SerializedName("message") val message: String,
                @SerializedName("user") val user: UserData
            )
            data class PendaftaranDetailResponse(
                val paket_nama: String,
                val total: Int,
                val metode: String,
                val tanggal: String
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
            data class LoginRequest(
                val email: String,
                val password: String
            )
            data class PaketKursus(
                val nama: String,
                val harga: String,
                val image: String
            )

            data class PaketKursusResponse(
                val id: Int,
                val nama: String,
                val harga: Int,
                val tipe: String,
                val image: String
            )
            data class RiwayatModel(
                val id: Int,

                @SerializedName("mobil_dipilih")
                val mobil: String,   // ✅ sekarang kebaca

                val paket: String,
                val harga: Int,
                val tanggal: String,
                val status: String,
                val image: String
            )
            data class DetailPemesananResponse(
                val id: Int,
                val judul: String,
                @SerializedName("mobil_dipilih")
                val mobil: String,
                val paket: String,
                val harga: Int,
                val tanggal: String?, // ✅ WAJIB nullable
                val status: String,

                @SerializedName("order_id")
                val orderId: String?,

                @SerializedName("metode_pembayaran")
                val metodePembayaran: String?,

                @SerializedName("payment_status")
                val paymentStatus: String,

                val image: String
            )


            data class RiwayatResponse(
                val id: Int,
                val judul: String,
                val paket: String,
                val harga: Int,
                val tanggal: String
            )

            // Request register
            data class RegisterRequest(
                val name: String,
                val email: String,
                val password: String,
                val password_confirmation: String
            )

            // Data user
            data class User(
                val id: Int,
                val name: String,
                val email: String,
                val emailConfirmedAt: String?,
                val token: String?
            )

            // snap midtrans
            data class SnapResponse(
                val snap_token: String,
                val order_id: String,
                val amount: Int
            )
            data class SnapRequest(
                val transaction_id: Int,
                val metode: String
            )

            data class PaymentStatusResponse(
                val status: String,
                val paket_nama: String?,
                val metode: String?,
                val total: Int?
            )
            data class NextTransactionResponse(
                val transaction_id: Int,
                val type: String,
                val amount: Int,
                val cicilan_ke: Int?,
                val total_cicilan: Int?
            )


            data class JadwalItem(
                val id: Int,
                val pertemuan_ke: Int,
                val tanggal: String,
                val jam: String,
                val status: String
            )

            data class JadwalRequest(
                val pendaftaran_id: Int,
                val pertemuan_ke: Int,
                val tanggal: String,
                val jam: String
            )

            data class JadwalResponse(
                val pendaftaran_id: Int,
                val total_pertemuan: Int,
                val sisa_pertemuan: Int,
                val jadwal: List<JadwalItem>
            )

            data class JamDipakaiResponse(
                val tanggal: String,
                val jam_dipakai: List<String>
            )

            data class VerifiedResponse(
                val verified: Boolean
            )


            data class FormSubmitResponse(
                val success: Boolean,
                val message: String?,
                val data: Any? = null,
                val pendaftaran_id: Int? = null,
                val transaction_id: Int? = null,// ✅ FIX UTAMA
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
                @SerializedName("access_token") val accessToken: String? = null,
                @SerializedName("token_type") val tokenType: String? = null,


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
                    return !accessToken.isNullOrEmpty()
                }

                fun getUserModel(): UserData? {
                    val u = user ?: data
                    if (u != null) {
                        val safeUrl = u.profilePhotoUrl
                            ?.takeIf { it.isNotBlank() }
                            ?.let { if (it.startsWith("https")) it else "${ApiClient.BASE_IMAGE_URL}/$it" }

                        return u.copy(profilePhotoUrl = safeUrl)
                    }
                    return null
                }



            }
            fun PaketKursusResponse.toPaketKursus(): PaketKursus {
                val imageUrl =
                    if (image.startsWith("https")) {
                        image
                    } else {
                        "${ApiClient.BASE_IMAGE_URL}/$image"
                    }

                return PaketKursus(
                    nama = nama,
                    harga = "Rp ${harga}",
                    image = imageUrl
                )
            }

            class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val builder = chain.request().newBuilder()
                        .addHeader("Accept", "application/json")

                    tokenProvider()?.let {
                        builder.addHeader("Authorization", "Bearer $it")
                    }

                    return chain.proceed(builder.build())
                }
            }


            // =============================
            // 3. AUTH API
            // =============================
            interface AuthApi {

                @POST("login")
                suspend fun loginUser(@Body data: Map<String, String>): Response<AuthResponse>

                @POST("register")
                suspend fun register(
                    @Body body: RegisterRequest
                ): Response<AuthResponse>



                @GET("user")
                suspend fun getUser(): Response<AuthResponse>

                @Multipart
                @POST("user/update")
                suspend fun updateProfile(
                    @Part("name") name: RequestBody,
                    @Part profile_photo: MultipartBody.Part?
                ): Response<UpdateProfileResponse>

                @POST("user/change-password")
                suspend fun changePassword(@Body data: Map<String, String>): Response<AuthResponse>

                @POST("email/resend")
                suspend fun sendVerificationEmail(): Response<AuthResponse>
                @POST("forgot-password")
                suspend fun forgotPassword(@Body data: Map<String, String>): Response<AuthResponse>

                @POST("v1/pendaftaran")
                suspend fun submitForm(
                    @Body data: FormRequest
                ): Response<FormSubmitResponse>


                @POST("bot/chat")
                suspend fun sendBotMessage(@Body request: BotRequest): Response<BotResponse>

                @POST("user/notification-preference")
                suspend fun updateNotifPreference(@Body body: Map<String, Boolean>): Response<AuthResponse>

                @GET("promos")
                suspend fun getPromos(): Response<List<PromoItem>>

                @GET("promos/{id}")
                suspend fun getPromoDetail(@Path("id") id: Int): Response<PromoItem>

                @POST("chat/send")
                suspend fun sendChat(@Body body: Map<String, Any>): Response<Unit>

                @GET("chat/{roomId}")
                suspend fun getChats(@Path("roomId") roomId: Int): Response<List<ChatMessage>>

                @POST("v1/payment/snap")
                suspend fun getSnapToken(
                    @Body request: SnapRequest
                ): Response<SnapResponse>
                @GET("v1/payment/status/{id}")
                suspend fun checkPaymentStatus(
                    @Path("id") pendaftaranId: Int
                ): Response<PaymentStatusResponse>

                @GET("v1/riwayat-pemesanan")
                suspend fun getRiwayatPemesanan(): Response<List<RiwayatModel>>


                @GET("v1/riwayat-pemesanan/{id}")
                suspend fun getDetailPemesanan(
                    @Path("id") id: Int
                ): Response<DetailPemesananResponse>

                @GET("paket-kursus")
                suspend fun getPaketKursus(): Response<List<PaketKursusResponse>>
                @GET("v1/pendaftaran/{id}")
                suspend fun getPendaftaranDetail(
                    @Path("id") id: Int
                ): Response<PendaftaranDetailResponse>

                @GET("v1/jadwal-pertemuan/{pendaftaran_id}")
                suspend fun getJadwal(
                    @Path("pendaftaran_id") pendaftaranId: Int
                ): Response<JadwalResponse>

                @POST("v1/jadwal")
                suspend fun ajukanJadwal(
                    @Body body: JadwalRequest
                ): Response<ResponseBody>

                @POST("v1/jadwal-pertemuan/{id}/selesai")
                suspend fun selesai(
                    @Path("id") id: Int
                ): Response<ResponseBody>

                @GET("v1/jadwal-pertemuan/jam-dipakai")
                suspend fun jamDipakai(
                    @Query("tanggal") tanggal: String
                ): Response<JamDipakaiResponse>
                @GET("v1/pendaftaran/aktif")
                suspend fun getPendaftaranAktif(): Response<PendaftaranAktifResponse>

                @GET("v1/transaction/next/{pendaftaran_id}")
                suspend fun getNextTransaction(
                    @Path("pendaftaran_id") id: Int
                ): Response<NextTransactionResponse>
                @GET("v1/pendaftaran/{id}/cicilan")
                suspend fun getRiwayatCicilan(
                    @Path("id") pendaftaranId: Int
                ): Response<RiwayatCicilanResponse>
                @POST("payment/retry/{id}")
                fun retryPayment(
                    @Path("id") transactionId: Int
                ): Response<SnapResponse>

            }

