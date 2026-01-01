package projectichif.DriveNusa

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import projectichif.DriveNusa.api.UserData
import java.io.File
import java.io.FileOutputStream


object UserLocal {

    private const val PREF_NAME = "user_pref"

    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER = "user_data"
    private const val KEY_USER_ID = "user_id"
    private fun photoKey(userId: Int) = "profile_photo_local_$userId"

    private const val KEY_FCM_TOKEN = "fcm_token"
    private const val KEY_PROFILE_UPDATED = "profile_just_updated"

    private val gson = Gson()

    // ================= CORE =================
    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ================= TOKEN =================
    fun saveToken(context: Context, token: String) =
        prefs(context).edit().putString(KEY_TOKEN, token).apply()

    fun getToken(context: Context): String? =
        prefs(context).getString(KEY_TOKEN, null)

    fun clearToken(context: Context) =
        prefs(context).edit().remove(KEY_TOKEN).apply()

    // ================= USER =================
    fun saveUser(context: Context, user: UserData) {
        if (user.id == null) return // ⛔ cegah user rusak
        prefs(context).edit()
            .putString(KEY_USER, gson.toJson(user))
            .putInt(KEY_USER_ID, user.id)
            .apply()
    }


    fun getUser(context: Context): UserData? =
        prefs(context).getString(KEY_USER, null)
            ?.let { gson.fromJson(it, UserData::class.java) }

    fun clearUser(context: Context) {
        prefs(context).edit()
            .remove(KEY_USER)
            .remove(KEY_USER_ID)
            .apply()
    }

    // ================= PROFILE PHOTO =================
    fun saveProfilePhotoLocal(context: Context, uri: Uri, userId: Int) {
        val file = File(context.filesDir, "profile_photo_$userId.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }

        prefs(context).edit()
            .putString(photoKey(userId), file.absolutePath)
            .apply()
    }

    fun getProfilePhotoLocalPath(context: Context, userId: Int): String? =
        prefs(context).getString(photoKey(userId), null)

    fun clearProfilePhoto(context: Context, userId: Int) {
        getProfilePhotoLocalPath(context, userId)?.let {
            File(it).takeIf { f -> f.exists() }?.delete()
        }
        prefs(context).edit().remove(photoKey(userId)).apply()
    }


    // ================= PROFILE UPDATED =================
    fun setProfileJustUpdated(context: Context, value: Boolean) =
        prefs(context).edit().putBoolean(KEY_PROFILE_UPDATED, value).apply()

    fun wasProfileJustUpdated(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PROFILE_UPDATED, false)

    fun clearProfileUpdatedFlag(context: Context) =
        prefs(context).edit().remove(KEY_PROFILE_UPDATED).apply()

    // ================= FCM =================
    fun saveFcmToken(context: Context, token: String) =
        prefs(context).edit().putString(KEY_FCM_TOKEN, token).apply()

    fun getFcmToken(context: Context): String? =
        prefs(context).getString(KEY_FCM_TOKEN, null)

    // ================= SESSION =================
    /** Dipakai saat LOGIN ulang / token mati */
    fun clearSession(context: Context) {
        clearToken(context)
        clearUser(context)
        // clearProfilePhoto(context) ❌ JANGAN DIHAPUS SAAT LOGOUT
    }


    /** Dipakai hanya saat LOGOUT total */
    fun clearAll(context: Context) {
        val userId = getUser(context)?.id
        clearSession(context)

        if (userId != null) {
            clearProfilePhoto(context, userId)
        }

        prefs(context).edit().remove(KEY_FCM_TOKEN).apply()
    }


}

