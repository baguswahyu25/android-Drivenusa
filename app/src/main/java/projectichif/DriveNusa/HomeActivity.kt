package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // 🔥 CEK TOKEN PERTAMA KALI ACTIVITY DIBUKA
        verifyToken()


        // BNB default fragment
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.nav_home
            loadFragment(HomeFragment())
        }

        // Bottom Navigation
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            setBottomNavigationVisibility(true)
            when (item.itemId) {
                R.id.nav_home -> { loadFragment(HomeFragment()); true }
                R.id.nav_trainer -> { loadFragment(KursusFragment()); true }
                R.id.nav_notifications -> { loadFragment(NotificationFragment()); true }
                R.id.nav_profile -> { loadFragment(ProfileFragment()); true }
                else -> false
            }
        }
    }

    // 🔥 CEK TOKEN SETIAP APLIKASI KEMBALI KE HOME
    override fun onResume() {
        super.onResume()
        verifyToken()
    }


    // ==========================================================
    // FUNGSI CEK TOKEN — DIPAKAI DI onCreate & onResume
    // ==========================================================
    private fun verifyToken() {
        lifecycleScope.launch {
            val isValid = AuthRepository.checkToken(this@HomeActivity)
            Log.d("HOME_CHECK", "isValid = $isValid")

            if (!isValid) {
                Toast.makeText(this@HomeActivity, "Token kadaluarsa. Silakan login ulang.", Toast.LENGTH_LONG).show()
                logout()
            }
        }
    }
    private fun logout() {
        UserLocal.clearToken(this)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        binding.bottomNavigationView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
