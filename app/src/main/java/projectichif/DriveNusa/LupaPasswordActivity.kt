package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import projectichif.DriveNusa.api.AuthRepository

class LupaPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnKirim: Button
    private lateinit var btnKembaliLogin: TextView
    private lateinit var logoApp: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)

        etEmail = findViewById(R.id.etEmail)
        btnKirim = findViewById(R.id.btnKirimKode)
        btnKembaliLogin = findViewById(R.id.btnKembaliLogin)
        logoApp = findViewById(R.id.logoApp)

        // 🔹 Tombol Kirim Link Reset
        btnKirim.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val response = AuthRepository.forgotPassword(email)

                withContext(Dispatchers.Main) {
                    if (response != null && (response.success == true || response.status == true)) {
                        Toast.makeText(
                            this@LupaPasswordActivity,
                            "Link reset password telah dikirim ke email.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@LupaPasswordActivity,
                            response?.message ?: "Gagal mengirim reset password",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // 🔹 Tombol kembali ke halaman login
        btnKembaliLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Menutup halaman lupa password agar tidak bisa kembali
        }
    }
}
