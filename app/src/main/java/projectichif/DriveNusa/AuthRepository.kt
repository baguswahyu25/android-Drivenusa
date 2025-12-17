package projectichif.DriveNusa.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import okhttp3.RequestBody.Companion.toRequestBody
import projectichif.DriveNusa.UserLocal
import projectichif.DriveNusa.utils.FileUtils
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import projectichif.DriveNusa.ApiClient
import retrofit2.Response
import projectichif.DriveNusa.api.BotRequest
import projectichif.DriveNusa.api.BotResponse




object AuthRepository {

    // BASE_URL sesuai emulator localhost (10.0.2.2)
    const val BASE_URL = "http://192.168.1.46:8000/api/"
    const val BASE_IMAGE_URL = "http://192.168.1.46:8000/storage"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()


    // Setup Retrofit
    private val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    // Getter API
    fun getAuthApi(): AuthApi = api

    // ----------------------------------------------------
    // FUNGSIONALITAS API
    // ----------------------------------------------------

    // REGISTER
    suspend fun registerUser(name: String, email: String, password: String): AuthResponse? {
        val body = mapOf(
            "name" to name,
            "email" to email,
            "password" to password,
            "password_confirmation" to password
        )

        val res = api.registerUser(body)
        return res.body() ?: AuthResponse(message = res.errorBody()?.string())
    }

    // LOGINa
    suspend fun loginUser(
        context: Context,
        email: String,
        password: String
    ): AuthResponse? {

        val res = api.loginUser(
            mapOf("email" to email, "password" to password)
        )

        if (res.isSuccessful) {
            val body = res.body()

            body?.user?.let { user ->

                UserLocal.clearSession(context)

                UserLocal.saveUser(context, user)
            }

            return body
        }

        return AuthResponse(message = res.errorBody()?.string())
    }


    // SEND VERIFICATION EMAIL
    // NOTE: backend kamu menerima Authorization header, jadi kita kirim "Bearer <token>"
    suspend fun sendVerificationEmail(token: String): AuthResponse? {
        val res = api.sendVerificationEmail("Bearer $token")
        return res.body() ?: AuthResponse(message = res.errorBody()?.string())
    }

    // GET USER
    // =======================================================================
    // GET USER
    // =======================================================================
    suspend fun getUser(context: Context): AuthResponse? = withContext(Dispatchers.IO) {
        val token = UserLocal.getToken(context) ?: return@withContext null

        try {
            val res = api.getUser("Bearer $token")

            when {
                res.isSuccessful -> res.body()

                res.code() == 401 || res.code() == 403 -> {
                    // TOKEN MATI → LOGOUT
                    UserLocal.clearSession(context)
                    null
                }

                else -> null
            }

        } catch (e: Exception) {
            Log.e("GET_USER", "Exception: ${e.message}")
            null
        }
    }



    suspend fun forgotPassword(email: String): AuthResponse? {
        val res = api.forgotPassword(mapOf("email" to email))
        return res.body() ?: AuthResponse(message = res.errorBody()?.string())
    }

    suspend fun updateUser(
        context: Context,
        name: String,
        photoUri: Uri? = null
    ): UserData? {

        val token = UserLocal.getToken(context) ?: return null
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val photoPart = photoUri?.let { uriToMultipart(context, it, "profile_photo") }

        return try {
            val res = api.updateProfile("Bearer $token", namePart, photoPart)

            if (res.isSuccessful) {
                val user = res.body()?.data?.user
                user?.let {
                    UserLocal.saveUser(context, it)
                    photoUri?.let { uri ->
                        UserLocal.saveProfilePhotoLocal(context, uri, it.id!!)
                    }
                }

                user
            } else null

        } catch (e: Exception) {
            null
        }
    }


    private fun uriToMultipart(context: Context, uri: Uri, field: String): MultipartBody.Part {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri) ?: "image/*"
        var fileName = "upload_${System.currentTimeMillis()}.jpg"

        resolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0)
                fileName = cursor.getString(nameIndex)
        }

        val tempFile = File(context.cacheDir, fileName)
        resolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }

        val reqBody = tempFile.asRequestBody(mime.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(field, fileName, reqBody)
    }

    suspend fun safeApiCall(
        context: Context,
        apiCall: suspend (token: String) -> Response<*>
    ): Pair<Boolean, String?> {
        val token = UserLocal.getToken(context) ?: return false to "Token tidak ditemukan"

        return try {
            val response = apiCall(token)
            if (response.isSuccessful) {
                true to null
            } else {
                if (response.code() == 401 || response.code() == 403) {
                    // token expired / unauthorized
                    false to "Token kadaluarsa. Silakan login ulang."
                } else {
                    false to response.errorBody()?.string()
                }
            }
        } catch (e: Exception) {
            false to e.message
        }
    }

    // ---------------- FORM NON-SIM ----------------
    suspend fun submitForm(context: Context, form: FormRequest): FormSubmitResponse? {
        val token = UserLocal.getToken(context) ?: return FormSubmitResponse(false, "Token tidak ditemukan")
        return try {
            val response = api.submitForm("Bearer $token", form)
            if (response.isSuccessful) response.body() ?: FormSubmitResponse(true, "Data berhasil disimpan")
            else {
                Log.e("FORM_ERROR", response.errorBody()?.string() ?: "No error body")
                FormSubmitResponse(false, "Validasi gagal")
            }
        } catch (e: Exception) {
            Log.e("FORM_EXCEPTION", e.message ?: "Unknown error")
            FormSubmitResponse(false, e.message ?: "Terjadi kesalahan")
        }
    }

    // ---------------- FORM SIM ----------------
    private fun str(v: String): RequestBody = v.toRequestBody("text/plain".toMediaTypeOrNull())
    private fun num(v: Int): RequestBody = v.toString().toRequestBody("text/plain".toMediaTypeOrNull())

    private fun createMultipart(context: Context, uri: Uri, fieldName: String): MultipartBody.Part {
        val resolver = context.contentResolver
        var fileName = "file_${System.currentTimeMillis()}.jpg"
        resolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && idx >= 0) fileName = cursor.getString(idx)
        }
        val mimeType = resolver.getType(uri) ?: "image/*"
        val tempFile = java.io.File.createTempFile("upload_", fileName.substringAfterLast('.', "jpg"), context.cacheDir)
        resolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        val reqBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fieldName, fileName, reqBody)
    }

    suspend fun submitFormSim(
        context: Context,
        paket: String,
        namaLengkap: String,
        tempatLahir: String,
        tanggalLahir: String,
        alamat: String,
        jenisKelamin: String,
        pekerjaan: String,
        mobilDipilih: String,
        metodePembayaran: String,
        opsiKredit: String?,
        harga: Int,
        pasPhotoUri: Uri,
        ktpUri: Uri
    ): FormSubmitResponse? {

        val token = UserLocal.getToken(context)
            ?: return FormSubmitResponse(false, "Token tidak ditemukan")

        return try {
            val pasFoto = createMultipart(context, pasPhotoUri, "pas_foto")
            val ktp = createMultipart(context, ktpUri, "ktp")

            val parts = HashMap<String, RequestBody>().apply {
                put("paket", str(paket))
                put("nama_lengkap", str(namaLengkap))
                put("tempat_lahir", str(tempatLahir))
                put("tanggal_lahir", str(tanggalLahir))
                put("alamat", str(alamat))
                put("jenis_kelamin", str(jenisKelamin))
                put("pekerjaan", str(pekerjaan))
                put("mobil_dipilih", str(mobilDipilih))
                put("metode_pembayaran", str(metodePembayaran))
                opsiKredit?.let { put("opsi_kredit", str(it)) }
                put("harga", num(harga))
                put("tipe_pendaftaran", str("sim"))
            }

            val res = api.submitFormSim("Bearer $token", parts, pasFoto, ktp)
            res.body() ?: FormSubmitResponse(false, "Server error")

        } catch (e: Exception) {
            FormSubmitResponse(false, e.message ?: "Koneksi gagal")
        }
    }



    // ============================================================
    // CHECK TOKEN
    // ============================================================

    suspend fun checkToken(context: Context): Boolean {
        val token = UserLocal.getToken(context)
        Log.d("CHECK_TOKEN", "Token = $token")
        if (token.isNullOrEmpty()) return false

        return try {
            val response = api.getUser("Bearer $token")
            Log.d("CHECK_TOKEN", "Response code = ${response.code()}")

            when (response.code()) {
                200 -> true  // token valid
                401, 403 -> false  // token expired / unauthorized
                else -> {
                    if (!response.isSuccessful) {
                        Log.e("CHECK_TOKEN", "Response code = ${response.code()}")
                    }

                    true  // jangan logout untuk error lain
                }
            }
        } catch (e: Exception) {
            Log.e("CHECK_TOKEN", "Exception: ${e.message}")
            true  // jangan logout karena network error
        }
    }
    suspend fun sendBotMessage(
        context: Context,
        message: String
    ): BotResponse? {

        val token = UserLocal.getToken(context) ?: return null

        return try {
            val response = api.sendBotMessage(
                token = "Bearer $token",
                request = BotRequest(message)
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("BOT_API", "Code: ${response.code()}")
                Log.e("BOT_API", response.errorBody()?.string() ?: "No error body")
                null

            }

        } catch (e: Exception) {
            null
        }
        Log.d("BOT_API", "Token = $token")

    }
    suspend fun changePassword(
        context: Context,
        oldPass: String,
        newPass: String
    ): AuthResponse {

        val token = UserLocal.getToken(context)
            ?: return AuthResponse(
                success = false,
                message = "Sesi berakhir, silakan login ulang"
            )

        return try {
            val response = api.changePassword(
                "Bearer $token",
                mapOf(
                    "old_password" to oldPass,
                    "new_password" to newPass,
                    "new_password_confirmation" to newPass
                )
            )

            when {
                response.isSuccessful -> {
                    AuthResponse(
                        success = true,
                        message = "Password berhasil diubah"
                    )
                }

                response.code() == 401 -> {
                    AuthResponse(
                        success = false,
                        message = "Password lama salah"
                    )
                }

                response.code() == 422 -> {
                    AuthResponse(
                        success = false,
                        message = "Password baru tidak valid"
                    )
                }

                else -> {
                    AuthResponse(
                        success = false,
                        message = "Gagal mengubah password, coba lagi"
                    )
                }
            }

        } catch (e: Exception) {
            AuthResponse(
                success = false,
                message = "Koneksi bermasalah, periksa internet"
            )
        }
    }

    suspend fun updateNotifPreference(
        context: Context,
        pengingat: Boolean,
        pembaruanAplikasi: Boolean,
        pembaruanProduk: Boolean,
        promo: Boolean
    ) {
        val token = UserLocal.getToken(context) ?: return

        api.updateNotifPreference(
            "Bearer $token",
            mapOf(
                "pengingat" to pengingat,
                "pembaruan_aplikasi" to pembaruanAplikasi,
                "pembaruan_produk" to pembaruanProduk,
                "promo" to promo
            )
        )
    }
    suspend fun sendChat(
        context: Context,
        roomId: Int,
        message: String
    ): Boolean {
        val token = UserLocal.getToken(context) ?: return false

        return try {
            val res = api.sendChat(
                "Bearer $token",
                mapOf(
                    "room_id" to roomId,
                    "message" to message
                )
            )
            res.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    suspend fun getChats(
        context: Context,
        roomId: Int
    ): List<ChatMessage>? {
        val token = UserLocal.getToken(context) ?: return null

        return try {
            val res = api.getChats("Bearer $token", roomId)
            if (res.isSuccessful) res.body() else null
        } catch (e: Exception) {
            null
        }
    }
//    suspend fun sendFcmToken(context: Context, fcmToken: String) {
//        val token = UserLocal.getToken(context) ?: return
//
//        try {
//            api.postFcmToken(
//                "Bearer $token",
//                mapOf("token" to fcmToken)
//            )
//        } catch (e: Exception) {
//            Log.e("FCM", "Gagal kirim token", e)
//        }
//    }


}




