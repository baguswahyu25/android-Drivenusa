package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import projectichif.DriveNusa.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: PaketKursusAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter

    // Data paket
    private val paketList = listOf(
        PaketKursus("Paket Manual", "Rp 1.150.000", R.drawable.img_paket_manual),
        PaketKursus("Paket Automatic", "Rp 1.250.000", R.drawable.img_paket_automatic),
        PaketKursus("Paket Manual Sim", "Rp 2.100.000", R.drawable.img_paket_manual_sim_baru),
        PaketKursus("Paket Automatic Sim", "Rp 2.200.000", R.drawable.img_paket_automatic_sim)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol back
        binding.btnBack.setOnClickListener { finish() }

        // Adapter paket (kosong dulu)
        adapter = PaketKursusAdapter(emptyList(), object : OnPaketClickListener {
            override fun onPilihClicked(paket: PaketKursus) {
                val intent = Intent(this@SearchActivity, SyaratActivity::class.java)
                intent.putExtra(SyaratActivity.EXTRA_PAKET_NAMA, paket.nama)
                startActivity(intent)
            }
        })

        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSearch.adapter = adapter

        // Tampilkan riwayat pertama kali
        showHistory()

        // Ketika user mengetik
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.isEmpty()) showHistory() else filter(text)
            }
        })

        // Simpan riwayat ketika user tekan Enter (Keyboard)
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val text = binding.etSearch.text.toString()
            SearchHistoryManager.saveQuery(this, text)
            filter(text)
            true
        }
    }


    // FILTER PAKET KURSUS
    private fun filter(text: String) {
        val filtered = paketList.filter {
            it.nama.contains(text, ignoreCase = true)
        }
        adapter.updateData(filtered)
        binding.rvSearch.adapter = adapter
    }


    // TAMPILKAN RIWAYAT PENCARIAN
    private fun showHistory() {
        val history = SearchHistoryManager.getHistory(this)

        if (history.isNotEmpty()) {
            historyAdapter = SearchHistoryAdapter(history) { selected ->
                binding.etSearch.setText(selected)
                filter(selected)
            }

            binding.rvSearch.adapter = historyAdapter
        } else {
            adapter.updateData(emptyList()) // kosong jika tidak ada riwayat
            binding.rvSearch.adapter = adapter
        }
    }
}
