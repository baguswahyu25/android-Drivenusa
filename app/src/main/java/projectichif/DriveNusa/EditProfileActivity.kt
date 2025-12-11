package projectichif.DriveNusa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var selectedImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCurrentUser()

        binding.btnChangePhoto.setOnClickListener { pickImage() }
        binding.btnSave.setOnClickListener { saveProfile() }
        binding.btnBack.setOnClickListener { finish() }
    }

    // ============================
    // PICK IMAGE FROM GALLERY
    // ============================
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImage = result.data?.data
                binding.imgProfile.setImageURI(selectedImage)
            }
        }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    // ============================
    // LOAD USER FROM API
    // ============================
    private fun loadCurrentUser() {
        lifecycleScope.launch {
            val response = AuthRepository.getUser(this@EditProfileActivity) // ✅ pakai context
            val user = response?.getUserModel() ?: return@launch

            binding.etName.setText(user.name ?: "")

            Glide.with(this@EditProfileActivity)
                .load(user.avatarUrl ?: "")
                .placeholder(R.drawable.ic_user)
                .into(binding.imgProfile)
        }
    }

    // ============================
    // SAVE PROFILE
    // ============================
    private fun saveProfile() {
        val name = binding.etName.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val response = AuthRepository.updateUser(
                token = "", // ❌ tidak perlu dikirim, di updateUser ambil token dari context
                name = name,
                avatar = selectedImage,
                context = this@EditProfileActivity
            )

            if (response == null || response.success == false) {
                Toast.makeText(this@EditProfileActivity,
                    response?.message ?: "Gagal menyimpan. Token mungkin kadaluarsa.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this@EditProfileActivity, "Berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
