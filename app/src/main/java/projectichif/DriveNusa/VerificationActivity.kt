package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding
    private var tokenUser: String = ""
    private var emailUser: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ============================
        // Ambil email dari Intent
        // ============================
        emailUser = intent.getStringExtra("email") ?: ""

        // ============================
        // Ambil token dari SharedPreferences
        // ============================
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        tokenUser = prefs.getString("token", "") ?: ""

        setupUI()

        binding.btnAlreadyVerified.setOnClickListener {
            animateButton(binding.btnAlreadyVerified)
            lifecycleScope.launch { sendVerifyRequest() }
        }
        binding.btnGoToLogin.setOnClickListener {
            animateButton(binding.btnGoToLogin)

            // Pindah ke halaman login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }


    private fun setupUI() {
        binding.imgLogo.alpha = 0f
        binding.imgLogo.animate().alpha(1f).setDuration(1000).start()

        binding.txtEmail.text = emailUser
        binding.tvVerificationTitle.text = "Verifikasi Email"
        binding.tvVerificationMessage.text = """
            Kami telah mengirim email verifikasi ke:
            $emailUser
        """.trimIndent()
        binding.btnAlreadyVerified.text = "Kirim Ulang Verifikasi"
    }

    private fun animateButton(view: View) {
        view.animate().scaleX(0.92f).scaleY(0.92f).alpha(0.6f)
            .setDuration(120)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).alpha(1f)
                    .setDuration(120).start()
            }.start()
    }

    private fun showLoading() {
        binding.btnAlreadyVerified.isEnabled = false
        binding.btnAlreadyVerified.text = "Mengirim..."
        binding.progressBarVerify.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.btnAlreadyVerified.isEnabled = true
        binding.btnAlreadyVerified.text = "Kirim Ulang Verifikasi"
        binding.progressBarVerify.visibility = View.GONE
    }

    private suspend fun sendVerifyRequest() {

        if (emailUser.isEmpty()) {
            Toast.makeText(this, "Email tidak ditemukan!", Toast.LENGTH_LONG).show()
            return
        }

        // Pastikan token ada — karena backend kamu mengharapkan Authorization header
        if (tokenUser.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan. Silakan login ulang atau cek koneksi.", Toast.LENGTH_LONG).show()
            return
        }

        showLoading()

        try {
            // Kirim ulang email verifikasi menggunakan token (AuthRepository akan menambahkan "Bearer ")
            val response = AuthRepository.sendVerificationEmail(tokenUser)

            hideLoading()

            if (response?.isSuccess() == true) {
                Toast.makeText(
                    this,
                    "Email verifikasi telah dikirim ke $emailUser",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    response?.message ?: "Gagal mengirim email verifikasi",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            hideLoading()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
