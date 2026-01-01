package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_TYPE = "extra_verification_type"

        const val TYPE_REGISTER = "REGISTER"
        const val TYPE_RESET_PASSWORD = "RESET_PASSWORD"
    }
    private lateinit var binding: ActivityVerificationBinding
    private var tokenUser: String = ""
    private var emailUser: String = ""
    private var verificationType = TYPE_REGISTER
    private var resendTimer: CountDownTimer? = null
    private val RESEND_COOLDOWN = 60_000L // 60 detik



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val lastRequestTime = getLastRequestTime()
        val elapsed = System.currentTimeMillis() - lastRequestTime

        if (elapsed < RESEND_COOLDOWN) {
            startResendCooldown(RESEND_COOLDOWN - elapsed)
        }
        // ============================
        // Ambil email dari Intent
        // ============================
        emailUser = intent.getStringExtra(EXTRA_EMAIL) ?: ""
        verificationType = intent.getStringExtra(EXTRA_TYPE) ?: TYPE_REGISTER

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

            // 🔥 HAPUS TOKEN
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
    private fun saveLastRequestTime() {
        val prefs = getSharedPreferences("verify", MODE_PRIVATE)
        prefs.edit()
            .putLong("last_request_time", System.currentTimeMillis())
            .apply()
    }
    private fun getLastRequestTime(): Long {
        val prefs = getSharedPreferences("verify", MODE_PRIVATE)
        return prefs.getLong("last_request_time", 0L)
    }
    private fun startResendCooldown(remainingTime: Long = RESEND_COOLDOWN) {

        resendTimer?.cancel()

        resendTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.btnAlreadyVerified.isEnabled = false
                binding.btnAlreadyVerified.text = "Kirim ulang ($seconds dtk)"
            }

            override fun onFinish() {
                binding.btnAlreadyVerified.isEnabled = true
                binding.btnAlreadyVerified.text = when (verificationType) {
                    TYPE_REGISTER -> "Kirim Ulang Verifikasi"
                    TYPE_RESET_PASSWORD -> "Kirim Ulang Link Reset"
                    else -> "Kirim Ulang"
                }
            }
        }.start()
    }


    private fun setupUI() {
        binding.txtEmail.text = emailUser

        when (verificationType) {

            TYPE_REGISTER -> {
                binding.tvVerificationTitle.text = "Verifikasi Email"
                binding.tvVerificationMessage.text =
                    "Kami telah mengirim email verifikasi ke:\n$emailUser"

                binding.btnAlreadyVerified.text = "Kirim Ulang Verifikasi"
            }

            TYPE_RESET_PASSWORD -> {
                binding.tvVerificationTitle.text = "Reset Password"
                binding.tvVerificationMessage.text =
                    "Kami telah mengirim link reset password ke:\n$emailUser"

                binding.btnAlreadyVerified.text = "Kirim Ulang Link Reset"
            }
        }
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

        showLoading()

        try {
            val response = when (verificationType) {

                TYPE_REGISTER -> AuthRepository.sendVerificationEmail()

                TYPE_RESET_PASSWORD -> AuthRepository.forgotPassword(emailUser)

                else -> null
            }

            hideLoading()

            if (response != null) {

                saveLastRequestTime()          // 🔒 simpan waktu
                startResendCooldown()          // ⏱ mulai 60 detik

                Toast.makeText(
                    this,
                    "Email berhasil dikirim ke $emailUser",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                Toast.makeText(this, "Gagal mengirim email", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            hideLoading()
            Toast.makeText(this, e.message ?: "Terjadi kesalahan", Toast.LENGTH_LONG).show()
        }

    }
    override fun onDestroy() {
        resendTimer?.cancel()
        super.onDestroy()
    }

}
