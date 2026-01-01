    package projectichif.DriveNusa

    import android.app.Activity
    import android.content.Intent
    import android.graphics.BitmapFactory
    import android.os.Bundle
    import android.util.Log
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
        private var currentUser: UserData? = null


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
            binding.itemCicilan.setOnClickListener {

                // cegah double click
                binding.itemCicilan.isEnabled = false
                showLoading(true)

                lifecycleScope.launch {
                    try {
                        val response = ApiClient.authApi.getPendaftaranAktif()

                        showLoading(false)
                        binding.itemCicilan.isEnabled = true

                        if (response.isSuccessful && response.body() != null) {

                            val pendaftaranId = response.body()!!.id
                            val intent = Intent(requireContext(), CicilanActivity::class.java)
                            intent.putExtra("extra_pendaftaran_id", pendaftaranId)
                            startActivity(intent)

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Belum ada pendaftaran aktif",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        showLoading(false)
                        binding.itemCicilan.isEnabled = true

                        Toast.makeText(
                            requireContext(),
                            "Gagal membuka cicilan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            binding.itemPengajuanJadwal.setOnClickListener {

                // cegah double click
                binding.itemPengajuanJadwal.isEnabled = false
                showLoading(true)

                lifecycleScope.launch {
                    try {
                        val response = ApiClient.authApi.getPendaftaranAktif()

                        showLoading(false)
                        binding.itemPengajuanJadwal.isEnabled = true

                        if (response.isSuccessful && response.body() != null) {

                            val pendaftaranId = response.body()!!.id
                            val intent = Intent(requireContext(), PengajuanJadwalActivity::class.java)
                            intent.putExtra("pendaftaran_id", pendaftaranId)
                            startActivity(intent)

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Belum ada pendaftaran aktif",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {

                        showLoading(false)
                        binding.itemPengajuanJadwal.isEnabled = true

                        Toast.makeText(
                            requireContext(),
                            "Gagal mengambil pendaftaran",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            binding.itemKeluarAkun.setOnClickListener {
                showLogoutConfirmation()
            }

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
        private fun showLoading(show: Boolean) {
            binding.loadingOverlay.visibility =
                if (show) View.VISIBLE else View.GONE
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
                currentUser = localUser
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
            binding.profileImage.setImageResource(R.drawable.ic_person)
        }
        private fun loadProfilePhoto(user: UserData) {
            val ctx = requireContext()
            val userId = user.id ?: return

            // 1️⃣ Coba load dari LOCAL DULU (INSTAN)
            val localPath = UserLocal.getProfilePhotoLocalPath(ctx, userId)
            if (!localPath.isNullOrEmpty() && File(localPath).exists()) {
                binding.profileImage.setImageBitmap(
                    BitmapFactory.decodeFile(localPath)
                )
                return // ⛔ STOP, jangan ke server
            }

            // 2️⃣ Kalau TIDAK ADA lokal → pakai SERVER
            if (!user.profilePhotoUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(user.profilePhotoUrl) // ❌ TANPA timestamp
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.profileImage)
            } else {
                // 3️⃣ Server null → hapus lokal & pakai default
                UserLocal.clearProfilePhoto(ctx, userId)
                binding.profileImage.setImageResource(R.drawable.ic_person)
            }
        }





        private fun openKeamananAkunFragment() {
            val fragment = KeamananAkunFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        private fun showLogoutConfirmation() {
            val ctx = requireContext()

            androidx.appcompat.app.AlertDialog.Builder(ctx)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                .setCancelable(false)
                .setPositiveButton("Ya") { _, _ ->
                    performLogout()
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss() // tetap di ProfileFragment
                }
                .show()
        }

        private fun performLogout() {
            val ctx = requireContext()

            UserLocal.clearSession(ctx)

            Toast.makeText(ctx, "Berhasil keluar akun", Toast.LENGTH_SHORT).show()

            val intent = Intent(ctx, LoginActivity::class.java)
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
