package projectichif.DriveNusa

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import projectichif.DriveNusa.databinding.ActivityFormPengajuanBinding
import java.util.Calendar

class FormPengajuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormPengajuanBinding
    private var selectedTanggal: String? = null
    private var selectedJam: String? = null

    // JAM LATIHAN YANG TERSEDIA
    private val listJam = listOf(
        "08:00",
        "09:00",
        "10:00",
        "11:00",
        "13:00",
        "14:00",
        "15:00",
        "16:00"
    )

    // Contoh DATA JAM YANG SUDAH DIPAKAI → harusnya dari API
    // Key = tanggal, Value = list jam yang sudah dipakai
    private val jamDipakai = hashMapOf(
        "2025-11-23" to listOf("09:00", "14:00") // contoh
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPengajuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val namaPertemuan = intent.getStringExtra("pertemuan_nama")
        binding.txtPertemuan.text = namaPertemuan ?: "-"

        setupTanggalPicker()
        renderJamButtons()

        binding.btnAjukan.setOnClickListener {
            ajukanJadwal()
        }
    }

    // ============================
    // PICKER TANGGAL
    // ============================
    private fun setupTanggalPicker() {
        binding.btnTanggal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dp = DatePickerDialog(this, { _, y, m, d ->
                selectedTanggal = String.format("%04d-%02d-%02d", y, m + 1, d)
                binding.btnTanggal.text = selectedTanggal
                renderJamButtons() // update jam setelah tanggal berubah
            }, year, month, day)

            dp.show()
        }
    }

    // ============================
    // TAMPILKAN JAM
    // ============================
    private fun renderJamButtons() {
        binding.layoutJam.removeAllViews()

        val used = jamDipakai[selectedTanggal] ?: listOf()

        listJam.forEach { jam ->

            val btn = Button(this)
            btn.text = jam
            btn.setPadding(20, 20, 20, 20)

            // style normal
            btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.white))

            // Jika jam sudah dipakai → disable
            if (used.contains(jam)) {
                btn.isEnabled = false
                btn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }

            btn.setOnClickListener {
                selectedJam = jam
                resetAllJamButtons()
                btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_700)
            }

            binding.layoutJam.addView(btn)
        }
    }

    private fun resetAllJamButtons() {
        for (i in 0 until binding.layoutJam.childCount) {
            val b = binding.layoutJam.getChildAt(i) as Button
            b.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
        }
    }

    // ============================
    // KIRIM PENGAJUAN
    // ============================
    private fun ajukanJadwal() {
        if (selectedTanggal == null) {
            Toast.makeText(this, "Pilih tanggal dulu!", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedJam == null) {
            Toast.makeText(this, "Pilih jam dulu!", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: KIRIM KE API
        // ======================
        // Kirim data:
        // - Pertemuan
        // - tanggal
        // - jam
        // ======================

        Toast.makeText(this, "Pengajuan terkirim!", Toast.LENGTH_LONG).show()
        val result = Intent()
        result.putExtra("pertemuan_index", intent.getIntExtra("pertemuan_index", -1))
        setResult(RESULT_OK, result)
        finish()

    }
}
