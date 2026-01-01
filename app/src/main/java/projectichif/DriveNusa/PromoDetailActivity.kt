package projectichif.DriveNusa

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository


class PromoDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_detail)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val imgPromo: ImageView = findViewById(R.id.imgPromo)
        val tvTitle: TextView = findViewById(R.id.tvTitle)
        val tvDesc: TextView = findViewById(R.id.tvDesc)
        val tvExpired: TextView = findViewById(R.id.tvExpired)

        btnBack.setOnClickListener { finish() }

        // ===============================
        // 🔥 1. TAMPILKAN DATA DARI INTENT (INSTAN)
        // ===============================
        tvTitle.text = intent.getStringExtra("TITLE") ?: "-"
        tvDesc.text = intent.getStringExtra("DESC") ?: "-"
        tvExpired.text = intent.getStringExtra("EXPIRED")?.let {
            "Berlaku sampai $it"
        } ?: "Tidak ada batas waktu"

        Glide.with(this)
            .load(intent.getStringExtra("IMAGE"))
            .placeholder(R.drawable.placeholder_promo)
            .error(R.drawable.placeholder_promo)
            .into(imgPromo)

        // ===============================
        // 🔄 2. OPTIONAL: REFRESH DARI API (KALAU MAU)
        // ===============================
        val promoId = intent.getIntExtra("PROMO_ID", -1)
        if (promoId != -1) {
            lifecycleScope.launch {
                try {
                    val response =ApiClient.authApi.getPromoDetail(promoId)

                    if (response.isSuccessful) {
                        val promo = response.body() ?: return@launch

                        // update data kalau API beda
                        tvTitle.text = promo.title
                        tvDesc.text = promo.description ?: tvDesc.text
                        tvExpired.text = promo.expiredAt?.let {
                            "Berlaku sampai $it"
                        } ?: tvExpired.text

                        Glide.with(this@PromoDetailActivity)
                            .load(intent.getStringExtra("IMAGE"))
                            .override(900, 350)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder_promo)
                            .error(R.drawable.placeholder_promo)
                            .into(imgPromo)


                    }
                } catch (e: Exception) {
                    Log.e("PROMO_DETAIL", e.message ?: "API error")
                }
            }
        }
    }
}
