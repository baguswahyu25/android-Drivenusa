package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.PaketKursus
import projectichif.DriveNusa.api.toPaketKursus
import projectichif.DriveNusa.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(), OnPaketClickListener {


    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: PaketKursusAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter

    private var paketList: List<PaketKursus> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        adapter = PaketKursusAdapter(
            paketList = emptyList(),
            isHome = false,
            listener = this// 🔥 PENTING
        )

        binding.rvSearch.layoutManager = GridLayoutManager(this, 2)
        binding.rvSearch.adapter = adapter

        // Load paket kursus dari API
        loadPaketKursus()

        // Search TextWatcher
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.isEmpty()) showHistory() else filter(text)
            }
        })

        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val text = binding.etSearch.text.toString()
            SearchHistoryManager.saveQuery(this, text)
            filter(text)
            true
        }
    }
    override fun onPilihClicked(paket: PaketKursus) {
        val intent = Intent(this, SyaratActivity::class.java)
        intent.putExtra(SyaratActivity.EXTRA_PAKET_NAMA, paket.nama)
        startActivity(intent)
    }

    private fun loadPaketKursus() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.authApi.getPaketKursus()
                if (response.isSuccessful) {
                    val paketResponseList = response.body() ?: emptyList()
                    paketList = paketResponseList.map { it.toPaketKursus() }
                    adapter.updateData(paketList)
                }
                Log.d("SEARCH", "Total paket = ${paketList.size}")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun filter(text: String) {
        val filtered = paketList.filter {
            it.nama.contains(text, ignoreCase = true)
        }
        adapter.updateData(filtered)
    }

    private fun showHistory() {
        val history = SearchHistoryManager.getHistory(this)

        if (history.isNotEmpty()) {
            historyAdapter = SearchHistoryAdapter(history) { selected ->
                binding.etSearch.setText(selected)
                filter(selected)
            }
            binding.rvSearch.adapter = historyAdapter
        } else {
            // ✅ KEMBALIKAN LIST PAKET, BUKAN KOSONGKAN
            binding.rvSearch.adapter = adapter
            adapter.updateData(paketList)
        }
    }

}
