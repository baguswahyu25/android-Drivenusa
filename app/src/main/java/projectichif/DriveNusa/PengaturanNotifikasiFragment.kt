package projectichif.DriveNusa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import projectichif.DriveNusa.UserLocal
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.data.NotificationPreferences
import projectichif.DriveNusa.databinding.FragmentPengaturanNotifikasiBinding
import projectichif.DriveNusa.viewmodel.PengaturanNotifVMFactory
import projectichif.DriveNusa.viewmodel.PengaturanNotifViewModel
import androidx.lifecycle.Lifecycle

class PengaturanNotifikasiFragment : Fragment() {

    private var _binding: FragmentPengaturanNotifikasiBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PengaturanNotifViewModel

    private var lastPengingat = false
    private var lastAplikasi = false
    private var lastProduk = false
    private var lastPromo = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengaturanNotifikasiBinding.inflate(inflater, container, false)

        val prefs = NotificationPreferences(requireContext())
        val factory = PengaturanNotifVMFactory(prefs)
        viewModel = ViewModelProvider(this, factory)[PengaturanNotifViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.pengingat.collectLatest {
                        lastPengingat = it
                        binding.switchPengingat.isChecked = it
                    }
                }

                launch {
                    viewModel.pembaruanAplikasi.collectLatest {
                        lastAplikasi = it
                        binding.switchPembaruanAplikasi.isChecked = it
                    }
                }

                launch {
                    viewModel.pembaruanProduk.collectLatest {
                        lastProduk = it
                        binding.switchPembaruanProduk.isChecked = it
                    }
                }

                launch {
                    viewModel.promo.collectLatest {
                        lastPromo = it
                        binding.switchPromo.isChecked = it
                    }
                }
            }
        }

        binding.switchPengingat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPengingat(isChecked)
            syncNotifPref()
        }

        binding.switchPembaruanAplikasi.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPembaruanAplikasi(isChecked)
            syncNotifPref()
        }

        binding.switchPembaruanProduk.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPembaruanProduk(isChecked)
            syncNotifPref()
        }

        binding.switchPromo.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPromo(isChecked)
            syncNotifPref()
        }
    } // ✅ onViewCreated DITUTUP DI SINI

    private fun syncNotifPref() {
        lifecycleScope.launch {
            AuthRepository.updateNotifPreference(
                requireContext(),
                lastPengingat,
                lastAplikasi,
                lastProduk,
                lastPromo
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
