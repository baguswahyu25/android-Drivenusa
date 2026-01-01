package projectichif.DriveNusa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.midtrans.sdk.corekit.core.MidtransSDK
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.CicilanItem
import projectichif.DriveNusa.api.SnapRequest

class CicilanActivity : AppCompatActivity() {

    private lateinit var rvCicilan: RecyclerView
    private lateinit var adapter: CicilanAdapter
    private val items = mutableListOf<CicilanItem>()

    private var pendaftaranId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cicilan)

        pendaftaranId = intent.getIntExtra("extra_pendaftaran_id", 0)

        if (pendaftaranId == 0) {
            toast("ID pendaftaran tidak valid")
            finish()
            return
        }

        rvCicilan = findViewById(R.id.rvCicilan)
        rvCicilan.layoutManager = LinearLayoutManager(this)

        adapter = CicilanAdapter(items) { cicilan ->
            if (cicilan.status == "pending") {
                bayarCicilan(cicilan.id)
            }
        }

        rvCicilan.adapter = adapter

        loadCicilan()
    }

    override fun onResume() {
        super.onResume()
        loadCicilan() // 🔄 auto refresh setelah bayar
    }

    // =====================
    // LOAD DATA
    // =====================
    private fun loadCicilan() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.authApi.getRiwayatCicilan(pendaftaranId)

                if (response.isSuccessful) {
                    items.clear()
                    items.addAll(response.body()?.items ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    toast("Gagal memuat cicilan")
                }
            } catch (e: Exception) {
                toast("Koneksi bermasalah")
            }
        }
    }

    // =====================
    // BAYAR CICILAN
    // =====================
    private fun bayarCicilan(transactionId: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.authApi.getSnapToken(
                    SnapRequest(
                        transaction_id = transactionId,
                        metode = "kredit"
                    )
                )

                if (response.isSuccessful) {
                    response.body()?.snap_token?.let { token ->
                        MidtransSDK.getInstance()
                            .startPaymentUiFlow(this@CicilanActivity, token)
                    }
                } else {
                    toast("Cicilan tidak bisa dibayar")
                }
            } catch (e: Exception) {
                toast("Gagal memulai pembayaran")
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}
