package projectichif.DriveNusa.ui.terms

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import projectichif.DriveNusa.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTermsBinding.inflate(layoutInflater)

        showAgreementDialog() // dialog muncul saat activity terbuka
    }

    private fun showAgreementDialog() {
        AlertDialog.Builder(this)
            .setTitle("Syarat & Ketentuan Pengguna 🚗")
            .setMessage(
                "Sebelum melanjutkan, harap baca dan setujui ketentuan berikut:\n\n" +
                        "1. Pengguna bertanggung jawab penuh atas setiap data yang dikirimkan.\n" +
                        "2. Aplikasi tidak menanggung kerusakan, kehilangan, atau penyalahgunaan akses akun.\n" +
                        "3. Semua fitur dan layanan dapat berubah sewaktu-waktu tanpa pemberitahuan.\n" +
                        "4. Data aktivitas dapat digunakan untuk peningkatan kualitas sistem.\n" +
                        "5. Dengan menekan tombol 'Setuju', Anda menyatakan memahami dan menerima seluruh kebijakan ini.\n\n" +
                        "Klik *Setuju* untuk melanjutkan."
            )
            .setCancelable(false)
            .setPositiveButton("Saya Setuju") { _, _ ->
                setContentView(binding.root) // baru halaman terms ditampilkan
                initClick()
            }
            .setNegativeButton("Tolak") { _, _ ->
                finish() // close
            }
            .show()
    }

    private fun initClick() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
