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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response



object AuthRepository {

    // BASE_URL sesuai emulator localhost (10.0.2.2)
    const val BASE_URL = "https://desainkito.web.id/api/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
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

    // LOGIN
    suspend fun loginUser(email: String, password: String): AuthResponse? {
        val res = api.loginUser(
            mapOf("email" to email, "password" to password)
        )
        return res.body() ?: AuthResponse(message = res.errorBody()?.string())
    }

    // LOGIN GOOGLE
    suspend fun loginWithGoogle(idToken: String): AuthResponse? {
        val res = api.loginWithGoogle(mapOf("token" to idToken))
        return res.body() ?: AuthResponse(message = res.errorBody()?.string())
    }

    // SEND VERIFICATION EMAIL
    // NOTE: backend kamu menerima Authorization header, jadi kita kirim "Bearer <token>"
    suspend fun sendVerificationEmail(token: String): AuthResponse? {
        val res = api.sendVerificationEmail("Bearer $token")
        return res.body() ?: AuthResponse(message = res.errorBody()?.string())
    }

    // GET USER
    suspend fun getUser(context: Context): AuthResponse? {
        val token = UserLocal.getToken(context) ?: return null   // ← di sini
        return try {
            val res = api.getUser("Bearer $token")
            if (res.isSuccessful) res.body() else {
                Log.e("GET_USER", "HTTP ${res.code()} -> ${res.errorBody()?.string()}")
                null
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

    // UPDATE USER (upload)
    suspend fun updateUser(
        token: String,
        name: String,
        avatar: Uri?,
        context: Context
    ): AuthResponse? {
        val token = UserLocal.getToken(context) ?: return AuthResponse(
            success = false,
            message = "Token tidak ditemukan. Silakan login ulang."
        )
        return try {

            val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val methodOverride = "PUT".toRequestBody("text/plain".toMediaTypeOrNull())

            val avatarPart: MultipartBody.Part? = if (avatar != null) {
                val file = File(FileUtils.getPath(context, avatar))
                val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())

                MultipartBody.Part.createFormData(
                    "avatar",
                    file.name,
                    reqFile
                )
            } else null

            val response = api.updateProfile(
                auth = "Bearer $token",
                method = methodOverride,
                name = nameBody,
                avatar = avatarPart
            )

            if (!response.isSuccessful) {
                Log.e("UPDATE_USER", "Error: ${response.errorBody()?.string()}")
            }

            if (response.isSuccessful) response.body() else null

        } catch (e: Exception) {
            Log.e("UPDATE_USER", "Exception: ${e.message}")
            null
        }
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
    ): AuthResponse? {
        val token = UserLocal.getToken(context) ?: return AuthResponse(success = false, message = "Token tidak ditemukan")
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

            val response = api.submitFormSim("Bearer $token", parts, pasFoto, ktp)
            if (!response.isSuccessful) {
                Log.e("FORM_SIM_ERROR", response.errorBody()?.string() ?: "Unknown error")
                return AuthResponse(success = false, message = "Validasi gagal") // <-- ganti status ke message
            }

            response.body()
        } catch (e: Exception) {
            Log.e("FORM_SIM_EXCEPTION", e.message ?: "Unknown error")
            return AuthResponse(success = false, message = e.message ?: "Terjadi kesalahan") // <-- ganti status ke message
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
                    Log.e("CHECK_TOKEN", "Unexpected response: ${response.errorBody()?.string()}")
                    true  // jangan logout untuk error lain
                }
            }
        } catch (e: Exception) {
            Log.e("CHECK_TOKEN", "Exception: ${e.message}")
            true  // jangan logout karena network error
        }
    }
}




