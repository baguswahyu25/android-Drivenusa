//package projectichif.DriveNusa.api
//
//import android.util.Log
//import io.github.jan.supabase.SupabaseClient
//import io.github.jan.supabase.createSupabaseClient
//import io.github.jan.supabase.auth.Auth
//import io.github.jan.supabase.auth.auth
//import io.github.jan.supabase.auth.providers.builtin.Email
//import io.github.jan.supabase.postgrest.Postgrest
//import io.github.jan.supabase.postgrest.postgrest
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.json.buildJsonObject
//import kotlinx.serialization.json.put
//
//
//object SupabaseService {
//
//    private const val SUPABASE_URL = "https://navdhmzjovdiuxenghzf.supabase.co"
//    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5hdmRobXpqb3ZkaXV4ZW5naHpmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI1MTE3NjAsImV4cCI6MjA3ODA4Nzc2MH0.5VLgTwvliBs2bToYTZ9OBI1dnhrYIjWZ9NFUL8NYlcE"
//
//    val client: SupabaseClient = createSupabaseClient(
//        supabaseUrl = SUPABASE_URL,
//        supabaseKey = SUPABASE_ANON_KEY
//    ) {
//        install(Auth)
//        install(Postgrest)
//    }
//
//    // ✅ REGISTER USER DENGAN REDIRECT URL (SUPABASE EMAIL VERIFIKASI)
//    suspend fun registerUser(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
//        return@withContext try {
//            val result = client.auth.signUpWith(Email) {
//                this.email = email
//                this.password = password
//                this.data = buildJsonObject {
//                    put("email_redirect_to", "https://drivenusa.vercel.app/verified")
//                }
//            }
//
//            Log.d("SupabaseService", "✅ SignUp success: $result")
//            true
//        } catch (e: Exception) {
//            Log.e("SupabaseService", "❌ Register error: ${e.message}", e)
//            false
//        }
//    }
//
//
//
//    suspend fun saveUserProfile(userId: String, username: String, email: String, phone: String?) = withContext(Dispatchers.IO) {
//        try {
//            val data = mapOf(
//                "id" to userId,
//                "username" to username,
//                "email" to email,
//                "phone" to phone
//            )
//            val result = client.postgrest["profiles"].insert(data)
//            Log.d("SupabaseService", "✅ Profile saved: $result")
//        } catch (e: Exception) {
//            Log.e("SupabaseService", "❌ Failed to save profile: ${e.message}", e)
//        }
//    }
//
//    suspend fun getCurrentUser() = withContext(Dispatchers.IO) {
//        try {
//            client.auth.retrieveUserForCurrentSession()
//        } catch (e: Exception) {
//            Log.e("SupabaseService", "❌ Get current user error: ${e.message}")
//            null
//        }
//    }
//}
