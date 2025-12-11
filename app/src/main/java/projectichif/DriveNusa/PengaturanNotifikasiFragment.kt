package projectichif.DriveNusa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import projectichif.DriveNusa.data.NotificationPreferences
import projectichif.DriveNusa.data.dataStore
import projectichif.DriveNusa.databinding.FragmentPengaturanNotifikasiBinding
import projectichif.DriveNusa.viewmodel.PengaturanNotifVMFactory
import projectichif.DriveNusa.viewmodel.PengaturanNotifViewModel

class PengaturanNotifikasiFragment : Fragment() {

    private var _binding: FragmentPengaturanNotifikasiBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PengaturanNotifViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengaturanNotifikasiBinding.inflate(inflater, container, false)

        // init datastore + viewmodel
        val prefs = NotificationPreferences(requireContext())
        val factory = PengaturanNotifVMFactory(prefs)
        viewModel = ViewModelProvider(this, factory)[PengaturanNotifViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Tombol back
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // ========== COLLECT DATASTORE (tanpa error) ==========
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {

                // Pengingat
                launch {
                    viewModel.pengingat.collectLatest {
                        binding.switchPengingat.isChecked = it
                    }
                }

                // Pembaruan Aplikasi
                launch {
                    viewModel.pembaruanAplikasi.collectLatest {
                        binding.switchPembaruanAplikasi.isChecked = it
                    }
                }

                // Pembaruan Produk
                launch {
                    viewModel.pembaruanProduk.collectLatest {
                        binding.switchPembaruanProduk.isChecked = it
                    }
                }

                // Promo
                launch {
                    viewModel.promo.collectLatest {
                        binding.switchPromo.isChecked = it
                    }
                }
            }
        }

        // ========== SAVE PERUBAHAN KE DATASTORE ==========
        binding.switchPengingat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPengingat(isChecked)
        }

        binding.switchPembaruanAplikasi.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPembaruanAplikasi(isChecked)
        }

        binding.switchPembaruanProduk.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPembaruanProduk(isChecked)
        }

        binding.switchPromo.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPromo(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
