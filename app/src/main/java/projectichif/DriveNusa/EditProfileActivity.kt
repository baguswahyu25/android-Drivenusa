    package projectichif.DriveNusa

    import android.app.Activity
    import android.content.Intent
    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import com.bumptech.glide.Glide
    import com.bumptech.glide.load.engine.DiskCacheStrategy
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import projectichif.DriveNusa.api.AuthRepository
    import projectichif.DriveNusa.api.AuthResponse
    import projectichif.DriveNusa.databinding.ActivityEditProfileBinding
    import projectichif.DriveNusa.utils.loadProfile
    import projectichif.DriveNusa.utils.saveProfilePhotoLocal
    import java.io.File
    import java.io.FileOutputStream

    class EditProfileActivity : AppCompatActivity() {

        private lateinit var binding: ActivityEditProfileBinding
        private var alreadyLoadedLocalPhoto = false
        private var isSaving = false
        private var selectedImage: Uri? = null
        private var currentPhotoUrl: String? = null
        private val userId: Int by lazy {
            UserLocal.getUser(this)?.id ?: 0
        }


        private val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let {
                    selectedImage = prepareImage(it)
                    renderPhoto()
                }
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityEditProfileBinding.inflate(layoutInflater)
            setContentView(binding.root)

            savedInstanceState?.getString("selected_image")?.let { selectedImage = Uri.parse(it) }

            loadFromLocalInstant()
            syncFromServerIfNeeded()


            binding.btnChangePhoto.setOnClickListener { pickImage() }
            binding.btnSave.setOnClickListener { saveProfile() }
            binding.btnBack.setOnClickListener { finish() }
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString("selected_image", selectedImage?.toString())
        }

        private fun loadFromLocalInstant() {
            val localUser = UserLocal.getUser(this) ?: return

            // 1️⃣ SET NAMA
            binding.etName.setText(localUser.name.orEmpty())

            // 2️⃣ LOAD FOTO LOKAL DULU (INSTAN)
            val localPath = UserLocal.getProfilePhotoLocalPath(this, localUser.id!!)
            if (!localPath.isNullOrEmpty() && File(localPath).exists()) {
                binding.imgProfile.setImageBitmap(
                    BitmapFactory.decodeFile(localPath)
                )
                alreadyLoadedLocalPhoto = true
            } else {
                binding.imgProfile.setImageResource(R.drawable.ic_person)
                alreadyLoadedLocalPhoto = false
            }
        }
        private fun syncFromServerIfNeeded() {
            lifecycleScope.launch {
                val user = AuthRepository.getUser(this@EditProfileActivity)
                    ?.getUserModel() ?: return@launch

                // Update nama kalau beda
                if (binding.etName.text.toString() != user.name) {
                    binding.etName.setText(user.name.orEmpty())
                }

                // Kalau lokal TIDAK ada → ambil server
                if (!alreadyLoadedLocalPhoto && !user.profilePhotoUrl.isNullOrEmpty()) {
                    Glide.with(this@EditProfileActivity)
                        .load(user.profilePhotoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(binding.imgProfile)
                }
            }
        }


        private fun renderPhoto() {
            when {
                selectedImage != null ->
                    binding.imgProfile.setImageURI(selectedImage)
                alreadyLoadedLocalPhoto ->
                    Unit // sudah ditampilkan
                else ->
                    binding.imgProfile.setImageResource(R.drawable.ic_person)
            }
        }


        private fun prepareImage(uri: Uri): Uri {
            val input = contentResolver.openInputStream(uri)!!
            val bitmap = BitmapFactory.decodeStream(input)
            input.close()
            val resized = Bitmap.createScaledBitmap(bitmap, 512, 512, true)
            val file = File(cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { resized.compress(Bitmap.CompressFormat.JPEG, 70, it)
            }
            return Uri.fromFile(file)
        }

        private fun pickImage() {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            pickImageLauncher.launch(intent)
        }

        private fun saveProfile() {
            if (isSaving) return

            val name = binding.etName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }

            isSaving = true
            showLoading(true)

            lifecycleScope.launch {

                val updatedUser = AuthRepository.updateUser(
                    context = this@EditProfileActivity,
                    name = name,
                    photoUri = selectedImage
                )

                if (updatedUser != null) {

                    // ✅ SIMPAN USER BARU
                    UserLocal.saveUser(this@EditProfileActivity, updatedUser)

                    // ✅ SIMPAN FOTO LOKAL JIKA ADA
                    selectedImage?.let {
                        UserLocal.saveProfilePhotoLocal(
                            this@EditProfileActivity,
                            it,
                            updatedUser.id!!
                        )
                    }

                    setResult(Activity.RESULT_OK)
                    finish()

                } else {
                    Toast.makeText(this@EditProfileActivity, "Gagal update profil", Toast.LENGTH_SHORT).show()
                }

                showLoading(false)
                isSaving = false
            }
        }


        private fun showLoading(show: Boolean) {
            binding.loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !show
            binding.btnChangePhoto.isEnabled = !show
        }




    }
