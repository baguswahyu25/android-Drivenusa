package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.ActivityLoginBinding
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Animasi logo
        binding.logoImage.alpha = 0f
        binding.logoImage.animate().alpha(1f).setDuration(1000).start()

        binding.btnLogin.setOnClickListener {
            animateButtonClick(binding.btnLogin)
            doLogin()
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.lupaPassword.setOnClickListener {
            startActivity(Intent(this, LupaPasswordActivity::class.java))
        }
    }

    // ================= UI =================
    private fun showLoading(show: Boolean) {
        binding.loadingOverlay.visibility =
            if (show) View.VISIBLE else View.GONE
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
            }.start()
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // ================= LOGIN =================

    private fun doLogin() {
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = "Email tidak valid"
            return
        }

        if (password.isEmpty()) {
            binding.inputPassword.error = "Password wajib diisi"
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = AuthRepository.loginUser(
                    this@LoginActivity,
                    email,
                    password
                )

                showLoading(false)

                if (!response?.accessToken.isNullOrEmpty()) {
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                } else {
                    showError(response?.message ?: "Login gagal")
                }

            } catch (e: Exception) {
                showLoading(false)
                showError("Terjadi kesalahan")
            }
        }
    }
}
