package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import projectichif.DriveNusa.databinding.FragmentKursusBinding

class KursusFragment : Fragment(), OnPaketClickListener {

    private var _binding: FragmentKursusBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupRecyclerView() {
        // List Paket (isi tetap, tidak diubah)
        val listPaket = listOf(
            PaketKursus("Paket Manual", "Rp. 1.150.000", R.drawable.img_paket_manual),
            PaketKursus("Paket Automatic", "Rp. 1.250.000", R.drawable.img_paket_automatic),
            PaketKursus("Paket Manual + SIM", "Rp. 2.150.000", R.drawable.img_paket_manual_sim_baru),
            PaketKursus("Paket Automatic + SIM", "Rp. 2.200.000", R.drawable.img_paket_automatic_sim)
        )

        val adapter = PaketKursusAdapter(listPaket, this)
        binding.rvKursusPaket.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKursusPaket.adapter = adapter
    }

    // =============================
    //  FITUR SEARCH DI FRAGMENT INI
    // =============================
    private fun setupSearchBarClick() {
        binding.cardSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            intent.putExtra("query", "")  // bisa diisi teks awal
            startActivity(intent)
        }
    }

    // SAAT KLIK TOMBOL PILIH PADA ITEM
    override fun onPilihClicked(paket: PaketKursus) {
        val intent = Intent(requireContext(), SyaratActivity::class.java)
        intent.putExtra(SyaratActivity.EXTRA_PAKET_NAMA, paket.nama)
        startActivity(intent)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
