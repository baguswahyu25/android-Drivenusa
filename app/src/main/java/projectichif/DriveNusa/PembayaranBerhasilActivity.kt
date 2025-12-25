package projectichif.DriveNusa.ui.pembayaran

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import projectichif.DriveNusa.databinding.ActivityPembayaranBerhasilBinding
import projectichif.DriveNusa.ui.RiwayatPemesananActivity
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import projectichif.DriveNusa.R


class PembayaranBerhasilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBerhasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paket = intent.getStringExtra("extra_paket") ?: "-"
        val metode = intent.getStringExtra("extra_metode") ?: "-"
        val total = intent.getIntExtra("extra_total", 0)


        binding.txtInfo.text = """
            Paket: $paket
            Metode: $metode
            Total: Rp $total
        """.trimIndent()

        // Play animasi Lottie (sekali)
        binding.lottieSuccess.apply {
            playAnimation()
            repeatCount = 0
        }

        // =============================
        // AUTO REDIRECT KE RIWAYAT
        // =============================
        binding.lottieSuccess.setAnimation(R.raw.succes_payment)
        binding.lottieSuccess.playAnimation()
        binding.lottieSuccess.repeatCount = 0

        binding.lottieSuccess.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                startActivity(
                    Intent(this@PembayaranBerhasilActivity, RiwayatPemesananActivity::class.java)
                )
                finishAffinity()
            }
        })


    }
}
