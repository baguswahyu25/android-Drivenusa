package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import projectichif.DriveNusa.R

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // 🔹 Ambil tipe verifikasi (register / forgot_password)
        val verificationType = intent.getStringExtra("verification_type") ?: "register"

        val lottieView: LottieAnimationView = findViewById(R.id.lottieLoading)
        lottieView.playAnimation()

        // 🔹 Delay minimal 3 detik (biar efek loading kelihatan)
        Handler(Looper.getMainLooper()).postDelayed({
            when (verificationType) {
                "register" -> {
                    // setelah registrasi selesai → kembali ke login
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                "forgot_password" -> {
                    // setelah reset password → langsung ke ubah password
                    startActivity(Intent(this, UbahPasswordActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            finish()
        }, 3000)
    }
}
