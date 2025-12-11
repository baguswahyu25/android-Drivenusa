package projectichif.DriveNusa.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import projectichif.DriveNusa.databinding.ActivityKenaliDriveNusaBinding

class KenaliDriveNusaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKenaliDriveNusaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKenaliDriveNusaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
    }
}