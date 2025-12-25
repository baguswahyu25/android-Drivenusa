    package projectichif.DriveNusa

    import android.app.Activity
    import android.content.Intent
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.lifecycleScope
    import com.bumptech.glide.Glide
    import com.bumptech.glide.load.engine.DiskCacheStrategy
    import kotlinx.coroutines.CancellationException
    import kotlinx.coroutines.Job
    import kotlinx.coroutines.launch
    import java.io.File
    import projectichif.DriveNusa.api.AuthRepository
    import projectichif.DriveNusa.databinding.FragmentProfileBinding
    import projectichif.DriveNusa.ui.KenaliDriveNusaActivity
    import projectichif.DriveNusa.ui.cs.CsChatFragment
    import projectichif.DriveNusa.ui.support.SupportFragment
    import projectichif.DriveNusa.ui.terms.TermsActivity
    import projectichif.DriveNusa.api.UserData
    import projectichif.DriveNusa.ui.RiwayatPemesananActivity

    class ProfileFragment : Fragment() {

        private var _binding: FragmentProfileBinding? = null
        private val binding get() = _binding!!
        private var loadUserJob: Job? = null

        private val editProfileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // TANDAI PROFIL BARU SAJA DIUPDATE
                    UserLocal.setProfileJustUpdated(requireContext(), true)
                    reloadUserData()
                }
            }



        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentProfileBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setupClickListeners()
        }

        override fun onStart() {
            super.onStart()
            // load user data dari lokal / server
            reloadUserData()
        }

        override fun onPause() {
            super.onPause()
            loadUserJob?.cancel()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        private fun setupClickListeners() {
            binding.btnChat.setOnClickListener { openChatFragment() }
            binding.itemPengajuanJadwal.setOnClickListener {
                startActivity(Intent(requireContext(), PengajuanJadwalActivity::class.java))
            }
            binding.itemKeluarAkun.setOnClickListener { performLogout() }
            binding.btnRiwayat.setOnClickListener {
                startActivity(Intent(requireContext(), RiwayatPemesananActivity::class.java))
            }
            binding.btnEditProfile.setOnClickListener {
                editProfileLauncher.launch(Intent(requireContext(), EditProfileActivity::class.java))
            }
            binding.itemKeamananAkun.setOnClickListener { openKeamananAkunFragment() }
            binding.itemNotifikasi.setOnClickListener { openPengaturanNotifikasiFragment() }
            binding.itemCustomerService.setOnClickListener { openCustomerServiceFragment() }
            binding.itemBeriDukungan.setOnClickListener { openSupportFragment() }
            binding.itemKenaliDriveNusa.setOnClickListener { openKenaliDriveNusaActivity() }
            binding.itemSyaratKetentuan.setOnClickListener {
                startActivity(Intent(requireContext(), TermsActivity::class.java))
            }
        }

        private fun reloadUserData() {
            val ctx = requireContext()

            viewLifecycleOwner.lifecycleScope.launch {

                val token = UserLocal.getToken(ctx)
                if (token.isNullOrEmpty()) {
                    resetProfileUI()
                    return@launch
                }

                val localUser = UserLocal.getUser(ctx)
                if (localUser == null) {
                    resetProfileUI()
                    return@launch
                }

                binding.tvUsername.text = localUser.name ?: "-"
                binding.tvUserContact.text = localUser.email ?: "-"
                loadProfilePhoto(localUser)

                // flag hanya untuk kontrol UI flow, bukan fetch ulang
                if (UserLocal.wasProfileJustUpdated(ctx)) {
                    UserLocal.setProfileJustUpdated(ctx, false)
                }
            }
        }


        private fun resetProfileUI() {
            binding.tvUsername.text = "-"
            binding.tvUserContact.text = "-"
            binding.profileImage.setImageResource(R.drawable.ic_user)
        }
        private fun loadProfilePhoto(user: UserData) {
            val ctx = requireContext()
            val userId = user.id ?: return
            val localPath = UserLocal.getProfilePhotoLocalPath(ctx, userId)

            when {
                !localPath.isNullOrEmpty() && File(localPath).exists() -> {
                    Glide.with(this)
                        .load(File(localPath))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.profileImage)
                }

                !user.profilePhotoUrl.isNullOrEmpty() -> {
                    Glide.with(this)
                        .load("${user.profilePhotoUrl}?t=${System.currentTimeMillis()}")
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.profileImage)
                }

                else -> binding.profileImage.setImageResource(R.drawable.ic_user)
            }
        }




        private fun openChatFragment() {
            val fragment = CsChatFragment()
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
            val ctx = context ?: return
            UserLocal.clearSession(requireContext())
            Toast.makeText(ctx, "Berhasil keluar akun", Toast.LENGTH_SHORT).show()
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

        private fun openPengaturanNotifikasiFragment() {
            val fragment = PengaturanNotifikasiFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
