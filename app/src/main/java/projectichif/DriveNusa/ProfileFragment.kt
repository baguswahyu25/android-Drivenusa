package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.FragmentProfileBinding
import projectichif.DriveNusa.ui.KenaliDriveNusaActivity
import projectichif.DriveNusa.ui.cs.CsChatFragment
import projectichif.DriveNusa.ui.support.SupportFragment
import projectichif.DriveNusa.ui.terms.TermsActivity


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()

        binding.btnChat.setOnClickListener {
            openChatFragment()
        }

        binding.itemPengajuanJadwal.setOnClickListener {
            startActivity(Intent(requireContext(), PengajuanJadwalActivity::class.java))
        }

        binding.itemKeluarAkun.setOnClickListener {
            performLogout()
        }

        binding.btnRiwayat.setOnClickListener {
            startActivity(Intent(requireContext(), RiwayatPemesananActivity::class.java))
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.itemKeamananAkun.setOnClickListener {
            openKeamananAkunFragment()
        }

        // ========= Hubungkan ke Pengaturan Notifikasi =========
        binding.itemNotifikasi.setOnClickListener {
            openPengaturanNotifikasiFragment()
        }
        binding.itemCustomerService.setOnClickListener {
            openCustomerServiceFragment()
        }
        binding.itemBeriDukungan.setOnClickListener {
            openSupportFragment()
        }
        binding.itemKenaliDriveNusa.setOnClickListener {
            openKenaliDriveNusaActivity()
        }
        binding.itemSyaratKetentuan.setOnClickListener {
            startActivity(Intent(requireContext(), TermsActivity::class.java))
        }

    }

    private fun openPengaturanNotifikasiFragment() {
        val fragment = PengaturanNotifikasiFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadUserData() {
        val context = requireContext()
        lifecycleScope.launch {
            val token = UserLocal.getToken(context)
            if (token.isNullOrEmpty()) {
                binding.tvUsername.text = "Pengguna"
                binding.tvUserContact.text = "- | -"
                return@launch
            }

            val response = AuthRepository.getUser(context)

            if (response == null) {
                binding.tvUsername.text = "Token kadaluarsa"
                binding.tvUserContact.text = "- | -"
            } else {
                val user = response.getUserModel()
                binding.tvUsername.text = user?.name ?: "Pengguna"
                binding.tvUserContact.text = user?.email ?: "-"
            }
        }
    }


    private fun openChatFragment() {
        val fragment = ChatListFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openKeamananAkunFragment() {
        val fragment = KeamananAkunFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun performLogout() {
        val context = context ?: return

        projectichif.DriveNusa.UserLocal.clearToken(context)
        Toast.makeText(context, "Berhasil keluar akun", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun openCustomerServiceFragment() {
        val fragment = CsChatFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun openSupportFragment() {
        val fragment = SupportFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun openKenaliDriveNusaActivity() {
        startActivity(Intent(requireContext(), KenaliDriveNusaActivity::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
