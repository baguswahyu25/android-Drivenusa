package projectichif.DriveNusa.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import projectichif.DriveNusa.utils.Prefs

class TokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401 || response.code == 403) {
            Prefs.clearToken(context) // ✅ BENAR
        }

        return response
    }
}

