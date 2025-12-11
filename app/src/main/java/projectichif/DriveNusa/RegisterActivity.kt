package projectichif.DriveNusa

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.ActivityRegisterBinding
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = "Sudah punya akun? Login"
        val span = SpannableString(text)

        span.setSpan(ForegroundColorSpan(Color.WHITE), 0, 17, 0)
        span.setSpan(ForegroundColorSpan(Color.parseColor("#f89331")), 18, text.length, 0)
        binding.tvLogin.text = span

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener {
            animateButtonClick(binding.btnRegister)
            doRegister()
        }
    }

    private fun animateButtonClick(view: View) {
        view.animate()
            .scaleX(0.92f)
            .scaleY(0.92f)
            .alpha(0.6f)
            .setDuration(120)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }

    private fun showLoading() {
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Loading..."
        binding.progressBarRegister.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.btnRegister.isEnabled = true
        binding.btnRegister.text = "Daftar"
        binding.progressBarRegister.visibility = View.GONE
    }

    private fun doRegister() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        when {
            username.isEmpty() -> {
                binding.etUsername.error = "Username wajib diisi"
                return
            }
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Email tidak valid"
                return
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password wajib diisi"
                return
            }
            password != confirmPassword -> {
                binding.etConfirmPassword.error = "Password tidak sama"
                return
            }
        }

        showLoading()

        lifecycleScope.launch {
            try {
                val response = AuthRepository.registerUser(username, email, password)
                hideLoading()

                if (response != null && response.isSuccess()) {

                    // ============================
                    // SIMPAN TOKEN DI SharedPreferences
                    // ============================
                    val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                    prefs.edit().putString("token", response.token).apply()

                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrasi berhasil! Silakan verifikasi email.",
                        Toast.LENGTH_LONG
                    ).show()

                    // ============================
                    // Pindah ke VerificationActivity
                    // hanya kirim email
                    // ============================
                    val intent = Intent(this@RegisterActivity, VerificationActivity::class.java)
                    intent.putExtra("email", email)   // <— hanya kirim email
                    startActivity(intent)

                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        response?.message ?: "Registrasi gagal",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                hideLoading()
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
