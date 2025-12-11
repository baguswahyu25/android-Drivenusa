package projectichif.DriveNusa.utils

import android.content.Context
import android.content.SharedPreferences

object Prefs {

    private const val PREF_NAME = "drive_nusa_prefs"
    private const val KEY_TOKEN = "auth_token"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        prefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return prefs(context).getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        prefs(context).edit().remove(KEY_TOKEN).apply()
    }
}
