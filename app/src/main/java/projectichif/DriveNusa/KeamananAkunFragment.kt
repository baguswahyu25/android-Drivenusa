package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.FragmentKeamananAkunBinding
import projectichif.DriveNusa.UserLocal

class KeamananAkunFragment : Fragment() {

    private var _binding: FragmentKeamananAkunBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeamananAkunBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnKonfirmasi.setOnClickListener {
            onKonfirmasiClicked()
        }

        return binding.root
    }

    private fun onKonfirmasiClicked() {
        val oldPass = binding.etOldPassword.text.toString().trim()
        val newPass = binding.etNewPassword.text.toString().trim()
        val confPass = binding.etConfirmPassword.text.toString().trim()

        when {
            oldPass.isEmpty() -> toast("Masukkan password lama")
            newPass.length < 6 -> toast("Password baru minimal 6 karakter")
            newPass != confPass -> toast("Konfirmasi password tidak cocok")
            else -> submitChangePassword(oldPass, newPass)
        }
    }
    private fun showLoading(show: Boolean) {
        binding.loadingOverlay.visibility =
            if (show) View.VISIBLE else View.GONE
    }

    private fun submitChangePassword(oldPass: String, newPass: String) {
        showLoading(true)
        binding.btnKonfirmasi.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            val result = AuthRepository.changePassword(
                requireContext(),
                oldPass,
                newPass
            )

            showLoading(false)
            binding.btnKonfirmasi.isEnabled = true

            if (result?.success == true) {
                toast("Password berhasil diubah, silakan login ulang")

                UserLocal.clearSession(requireContext())

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                requireActivity().finish()
            } else {
                toast(result?.message ?: "Gagal mengubah password")
            }
        }
    }



    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
