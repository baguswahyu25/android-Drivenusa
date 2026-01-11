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
        // Animasi logo
        binding.logoImage.alpha = 0f
        binding.logoImage.animate().alpha(1f).setDuration(1000).start()
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

    fun showLoading(show: Boolean) {
        binding.loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
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

        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = AuthRepository.registerUser(
                    this@RegisterActivity,
                    username,
                    email,
                    password
                )

                showLoading(false)

                if (response != null && response.isSuccess()) {

                    val intent = Intent(this@RegisterActivity, VerificationActivity::class.java)
                    intent.putExtra(VerificationActivity.EXTRA_EMAIL, email)
                    intent.putExtra(
                        VerificationActivity.EXTRA_TYPE,
                        VerificationActivity.TYPE_REGISTER
                    )

                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity,
                        response?.message ?: "Registrasi gagal",
                        Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(this@RegisterActivity,
                    "Terjadi kesalahan",
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}
