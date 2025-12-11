package projectichif.DriveNusa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import projectichif.DriveNusa.databinding.FragmentKeamananAkunBinding  // ganti package sesuai project kamu

class KeamananAkunFragment : Fragment() {

    private var _binding: FragmentKeamananAkunBinding? = null
    private val binding get() = _binding!!  // aman karena hanya dipakai antara onCreateView – onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeamananAkunBinding.inflate(inflater, container, false)
        val view = binding.root

        // Tombol back
        binding.btnBack.setOnClickListener {
            try { findNavController().popBackStack() } catch (e: Exception) { activity?.onBackPressed() }
        }

        // Tombol konfirmasi
        binding.btnKonfirmasi.setOnClickListener {
            onKonfirmasiClicked()
        }

        return view
    }

    private fun onKonfirmasiClicked() {
        val oldPass = binding.etOldPassword.text?.toString()?.trim().orEmpty()
        val newPass = binding.etNewPassword.text?.toString()?.trim().orEmpty()
        val confPass = binding.etConfirmPassword.text?.toString()?.trim().orEmpty()

        when {
            oldPass.isEmpty() -> {
                Toast.makeText(requireContext(), "Masukkan password lama", Toast.LENGTH_SHORT).show()
            }
            newPass.length < 6 -> {
                Toast.makeText(requireContext(), "Password baru minimal 6 karakter", Toast.LENGTH_SHORT).show()
            }
            newPass != confPass -> {
                Toast.makeText(requireContext(), "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // TODO: hubungkan ke backend / Supabase / API update password
                Toast.makeText(requireContext(), "Password berhasil diubah (simulasi)", Toast.LENGTH_SHORT).show()

                // kembali ke profile
                try { findNavController().popBackStack() } catch (e: Exception) { activity?.onBackPressed() }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}