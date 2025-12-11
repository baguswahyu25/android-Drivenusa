//package projectichif.DriveNusa
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import android.widget.Toast
//
//class DeepLinkReceiverActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val uri: Uri? = intent?.data
//        if (uri != null) {
//            // Cek apakah token ada di query parameter atau fragment
//            val tokenFromQuery = uri.getQueryParameter("access_token")
//            val tokenFromFragment = uri.fragment
//                ?.substringAfter("access_token=")
//                ?.substringBefore("&")
//
//            val accessToken = tokenFromQuery ?: tokenFromFragment
//
//            if (accessToken != null) {
//                Log.d("DeepLinkReceiver", "✅ Access Token: $accessToken")
//                Toast.makeText(this, "Verifikasi email berhasil!", Toast.LENGTH_LONG).show()
//            } else {
//                Log.w("DeepLinkReceiver", "⚠️ Tidak menemukan access_token di URI: $uri")
//                Toast.makeText(this, "Link verifikasi tidak valid.", Toast.LENGTH_LONG).show()
//            }
//        } else {
//            Log.e("DeepLinkReceiver", "❌ URI kosong, tidak ada data dari Supabase.")
//        }
//
//        // Setelah verifikasi berhasil, arahkan ke halaman login atau loading
//        startActivity(Intent(this, LoadingActivity::class.java))
//        finish()
//    }
//}
