package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.PaketKursus
import projectichif.DriveNusa.api.PaketKursusResponse
import projectichif.DriveNusa.databinding.FragmentKursusBinding
import androidx.fragment.app.activityViewModels

class KursusFragment : Fragment(), OnPaketClickListener {

    private var _binding: FragmentKursusBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PaketKursusAdapter
    private val viewModel: PaketKursusViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKursusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchBarClick()
        loadPaketKursus()
        binding.rvKursusPaket.adapter = ShimmerAdapter(5)

        viewModel.paketKursus.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                binding.rvKursusPaket.adapter = adapter
                adapter.updateData(list)
            }
        }

        viewModel.loadPaketKursus()

    }

    private fun setupRecyclerView() {
        adapter = PaketKursusAdapter(listOf(), this)
        binding.rvKursusPaket.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKursusPaket.adapter = adapter
    }

    private fun setupSearchBarClick() {
        binding.cardSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            intent.putExtra("query", "")
            startActivity(intent)
        }
    }

    private fun loadPaketKursus() {

        lifecycleScope.launch {
            try {
                val response = ApiClient.authApi.getPaketKursus()
                if (response.isSuccessful) {
                    val list = response.body()?.map {
                        PaketKursus(
                            nama = it.nama,
                            harga = "Rp. ${it.harga}",
                            image = "${ApiClient.BASE_IMAGE_URL}/${it.image}"
                        )
                    } ?: emptyList()

                    // 🔥 KEMBALIKAN ADAPTER ASLI
                    binding.rvKursusPaket.adapter = adapter
                    adapter.updateData(list)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    override fun onPilihClicked(paket: PaketKursus) {
        val intent = Intent(requireContext(), SyaratActivity::class.java)
        intent.putExtra(
            SyaratActivity.EXTRA_PAKET_NAMA,
            paket.nama   // 🔥 HARUS ADA ISI
        )
        startActivity(intent)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
