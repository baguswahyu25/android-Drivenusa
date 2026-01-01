package projectichif.DriveNusa.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import projectichif.DriveNusa.ApiClient
import projectichif.DriveNusa.DetailPemesananActivity
import projectichif.DriveNusa.PaymentActivity
import projectichif.DriveNusa.adapter.RiwayatAdapter
import projectichif.DriveNusa.databinding.ActivityRiwayatPemesananBinding

class RiwayatPemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatPemesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.rvRiwayat.layoutManager = LinearLayoutManager(this)

        loadRiwayat()
    }

    private fun showShimmer(show: Boolean) {
        if (show) {
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.shimmerLayout.startShimmer()
            binding.rvRiwayat.visibility = View.GONE
        } else {
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.rvRiwayat.visibility = View.VISIBLE
        }
    }

    private fun loadRiwayat() {
        showShimmer(true)

        lifecycleScope.launch {
            try {
                val res = ApiClient.authApi.getRiwayatPemesanan()
                showShimmer(false)

                val data = res.body() ?: emptyList()

                binding.rvRiwayat.adapter =
                    RiwayatAdapter(
                        data,
                        onPendingClick = { retryPayment(it.id) },
                        onDetailClick = {
                            startActivity(
                                Intent(this@RiwayatPemesananActivity,
                                    DetailPemesananActivity::class.java
                                ).putExtra("id", it.id)
                            )
                        }
                    )

            } catch (e: Exception) {
                showShimmer(false)
            }
        }
    }
    override fun onPause() {
        super.onPause()
        binding.shimmerLayout.stopShimmer()
    }


    override fun onResume() {
        super.onResume()
        loadRiwayat()
    }
    private fun retryPayment(transactionId: Int) {
        lifecycleScope.launch {
            try {
                val res = ApiClient.authApi.retryPayment(transactionId)

                if (res.isSuccessful) {
                    val token = res.body()!!.snap_token

                    val intent = Intent(
                        this@RiwayatPemesananActivity,
                        PaymentActivity::class.java
                    )
                    intent.putExtra("SNAP_TOKEN", token)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(this@RiwayatPemesananActivity,
                    "Gagal membuka pembayaran",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



}
