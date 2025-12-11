package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.ActivityLoginBinding
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(kotlin.time.ExperimentalTime::class)
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_GOOGLE_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AUTO LOGIN — jika token tersimpan langsung ke Home
        val savedToken = UserLocal.getToken(this)
        if (!savedToken.isNullOrEmpty()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.orange)

        // Logo animasi
        binding.logoImage.alpha = 0f
        binding.logoImage.animate().alpha(1f).setDuration(1000).start()

        // Tombol Login Email/Password
        binding.btnLogin.setOnClickListener {
            animateButtonClick(binding.btnLogin)
            doLogin()
        }

        // Google Sign-In Setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.google_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Pindah ke register
        binding.daftarText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.lupaPassword.setOnClickListener { // buka LupaPasswordActivity
            startActivity(Intent(this, LupaPasswordActivity::class.java)) }

    }

    // ANIMASI KLIK TOMBOL
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

    // TAMPILKAN LOADING DI TOMBOL LOGIN
    private fun showLoading() {
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Loading..."
        binding.btnLogin.alpha = 0.7f
        binding.progressBarLogin.visibility = View.VISIBLE
    }

    // SEMBUNYIKAN LOADING
    private fun hideLoading() {
        binding.btnLogin.isEnabled = true
        binding.btnLogin.text = "Login"
        binding.btnLogin.alpha = 1f
        binding.progressBarLogin.visibility = View.GONE
    }

    // HANDLE GOOGLE LOGIN RESULT
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken != null) {
                    loginDenganGoogleKeServer(idToken)
                } else {
                    Toast.makeText(this, "Token Google kosong!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: ApiException) {
                Toast.makeText(this, "Login Google gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // KIRIM TOKEN GOOGLE KE SERVER
    private fun loginDenganGoogleKeServer(idToken: String) {
        showLoading()

        lifecycleScope.launch {
            val response = AuthRepository.loginWithGoogle(idToken)

            hideLoading()

            if (response != null && response.success == true && response.token != null) {
                UserLocal.saveToken(this@LoginActivity, response.token!!)
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Login Google gagal!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // LOGIN EMAIL & PASSWORD
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

        showLoading()

        lifecycleScope.launch {
            try {
                val response = AuthRepository.loginUser(email, password)

                hideLoading()

                if (response != null && response.isSuccess() && response.token != null) {
                    UserLocal.saveToken(this@LoginActivity, response.token!!)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        response?.message ?: "Login gagal!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: UnknownHostException) {
                hideLoading()
                Toast.makeText(this@LoginActivity, "Tidak terhubung ke server", Toast.LENGTH_LONG).show()
            } catch (e: SocketTimeoutException) {
                hideLoading()
                Toast.makeText(this@LoginActivity, "Timeout!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                hideLoading()
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
