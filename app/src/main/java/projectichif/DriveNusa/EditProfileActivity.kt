package projectichif.DriveNusa

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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

        loadFromLocalFirst()
        loadCurrentUser()

        binding.btnChangePhoto.setOnClickListener { pickImage() }
        binding.btnSave.setOnClickListener { saveProfile() }
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selected_image", selectedImage?.toString())
    }

    private fun loadFromLocalFirst() {
        if (userId == 0) return

        val localPath = UserLocal.getProfilePhotoLocalPath(this, userId)
        if (!localPath.isNullOrEmpty() && File(localPath).exists()) {
            val bitmap = BitmapFactory.decodeFile(localPath)
            bitmap?.let {
                binding.imgProfile.setImageBitmap(it)
                alreadyLoadedLocalPhoto = true
            }
        }
    }


    private fun loadCurrentUser() {
        lifecycleScope.launch {
            if (isSaving) return@launch
            val user = AuthRepository.getUser(this@EditProfileActivity)?.getUserModel() ?: return@launch
            if (isSaving) return@launch
            binding.etName.setText(user.name.orEmpty())
            if (!alreadyLoadedLocalPhoto && selectedImage == null) {
                currentPhotoUrl = user.profilePhotoUrl
                renderPhoto()
            }
        }
    }

    private fun renderPhoto() {
        when {
            selectedImage != null -> binding.imgProfile.setImageURI(selectedImage)
            !currentPhotoUrl.isNullOrEmpty() && !alreadyLoadedLocalPhoto ->
                binding.imgProfile.loadProfile(currentPhotoUrl)
            else -> binding.imgProfile.setImageResource(R.drawable.ic_user)
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

            val user = AuthRepository.updateUser(
                this@EditProfileActivity,
                name,
                selectedImage
            )

            if (user != null) {

                UserLocal.setProfileJustUpdated(this@EditProfileActivity, true)
                Toast.makeText(this@EditProfileActivity, "Profil diperbarui", Toast.LENGTH_SHORT).show()

                setResult(Activity.RESULT_OK)
                delay(300)
                finish()

            } else {
                Toast.makeText(
                    this@EditProfileActivity,
                    "Gagal update profil",
                    Toast.LENGTH_SHORT
                ).show()
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
