package projectichif.DriveNusa.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import projectichif.DriveNusa.ApiClient
import projectichif.DriveNusa.DetailPemesananActivity
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

    private fun loadRiwayat() {
        lifecycleScope.launch {
            val response = ApiClient.authApi.getRiwayatPemesanan()
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                binding.rvRiwayat.adapter = RiwayatAdapter(response.body()!!) { selected ->
                    val intent = Intent(this@RiwayatPemesananActivity, DetailPemesananActivity::class.java)
                    intent.putExtra("id", selected.id)
                    startActivity(intent)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        loadRiwayat()
    }


}
