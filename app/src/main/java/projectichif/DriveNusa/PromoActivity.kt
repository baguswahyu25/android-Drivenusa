    package projectichif.DriveNusa

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import android.widget.ImageView
    import androidx.lifecycle.lifecycleScope
    import kotlinx.coroutines.launch
    import projectichif.DriveNusa.api.AuthRepository

    private var fromNotif = false
    private var notifPromoId = -1

    class PromoActivity : AppCompatActivity() {

        private lateinit var promoAdapter: PromoAdapter
        private val promoList = mutableListOf<PromoItem>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_promo)

            findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
            fromNotif = intent.getBooleanExtra("FROM_NOTIF", false)
            notifPromoId = intent.getIntExtra("PROMO_ID", -1)

            val rv = findViewById<RecyclerView>(R.id.rvPromo)
            rv.layoutManager = LinearLayoutManager(this)

            promoAdapter = PromoAdapter(promoList)
            rv.adapter = promoAdapter

            // 🔥 SHIMMER MUNCUL INSTAN
            promoAdapter.showLoading()

            fetchPromos()
        }
        private fun openPromoDetail(promo: PromoItem) {
            val intent = Intent(this, PromoDetailActivity::class.java)
            intent.putExtra("PROMO_ID", promo.id)
            intent.putExtra("TITLE", promo.title)
            intent.putExtra("DESC", promo.description)
            intent.putExtra("IMAGE", promo.imageUrl)
            intent.putExtra("EXPIRED", promo.expiredAt)
            startActivity(intent)
        }
        private fun fetchPromos() {

            // 🔥 JIKA SUDAH ADA CACHE → LANGSUNG PAKAI
            PromoCache.promos?.let { cached ->
                promoAdapter.submitData(cached)

                if (fromNotif && notifPromoId != -1) {
                    cached.find { it.id == notifPromoId }?.let {
                        openPromoDetail(it)
                        fromNotif = false
                    }
                }
                return
            }

            // 🔄 JIKA BELUM ADA → LOAD API SEKALI
            lifecycleScope.launch {
                try {
                    val response = ApiClient.authApi.getPromos()
                    if (response.isSuccessful) {
                        val data = response.body() ?: emptyList()

                        PromoCache.promos = data // ✅ SIMPAN CACHE
                        promoAdapter.submitData(data)

                        if (fromNotif && notifPromoId != -1) {
                            data.find { it.id == notifPromoId }?.let {
                                openPromoDetail(it)
                                fromNotif = false
                            }
                        }
                    } else {
                        promoAdapter.submitData(emptyList())
                    }
                } catch (e: Exception) {
                    promoAdapter.submitData(emptyList())
                }
            }
        }


    }


