package projectichif.DriveNusa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UbahPasswordActivity : AppCompatActivity() {

    private lateinit var etPasswordBaru: EditText
    private lateinit var etKonfirmasi: EditText
    private lateinit var btnSimpan: Button
    private var oobCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_password)

        etPasswordBaru = findViewById(R.id.etPasswordBaru)
        etKonfirmasi = findViewById(R.id.etKonfirmasiPassword)
        btnSimpan = findViewById(R.id.btnKonfirmasi)

        // Ambil kode verifikasi (oobCode) dari link di email
        val data: Uri? = intent?.data
        oobCode = data?.getQueryParameter("oobCode")

        btnSimpan.setOnClickListener {
            val pass = etPasswordBaru.text.toString().trim()
            val konfirmasi = etKonfirmasi.text.toString().trim()

            if (pass.isEmpty() || konfirmasi.isEmpty()) {
                etPasswordBaru.error = "Isi semua field"
                return@setOnClickListener
            }

            if (pass != konfirmasi) {
                etKonfirmasi.error = "Password tidak sama"
                return@setOnClickListener
            }

            if (oobCode == null) {
                Toast.makeText(this, "Kode verifikasi tidak ditemukan", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }
    }
}
