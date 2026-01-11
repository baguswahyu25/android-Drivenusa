    package projectichif.DriveNusa

    import android.app.Activity
    import android.content.Intent
    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import projectichif.DriveNusa.databinding.ActivityPengajuanJadwalBinding
    import projectichif.DriveNusa.utils.JadwalPrefs
    import androidx.lifecycle.lifecycleScope
    import kotlinx.coroutines.launch
    import projectichif.DriveNusa.ApiClient
    import projectichif.DriveNusa.api.PendaftaranAktifResponse


    class PengajuanJadwalActivity : AppCompatActivity() {

        private lateinit var binding: ActivityPengajuanJadwalBinding
        private lateinit var adapter: PertemuanAdapter
        private val pertemuanList = mutableListOf<PertemuanModel>()
        private var pendaftaranId: Int = 0

        private val api by lazy {
            ApiClient.authApi
        }


        companion object {
            const val REQ_PENGAJUAN = 2001
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityPengajuanJadwalBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnBack.setOnClickListener { finish() }

            // Ambil status dari SharedPreferences
            val savedStatus = JadwalPrefs.getStatusList(this)

            // Buat 14 pertemuan
            for (i in 0 until 14) {
                pertemuanList.add(
                    PertemuanModel("Pertemuan ${i + 1}", savedStatus[i])
                )
            }
            pendaftaranId = intent.getIntExtra("pendaftaran_id", 0)

            if (pendaftaranId == 0) {
                finish()
                return
            }
            lifecycleScope.launch {

                val response = api.getPendaftaranAktif()
            }

            lifecycleScope.launch {
                try {
                    val response = api.getJadwal(pendaftaranId)
                    if (response.isSuccessful) {
                        val body = response.body()

                        body?.jadwal?.forEach {
                            val index = it.pertemuan_ke - 1
                            if (index in 0..13) {
                                JadwalPrefs.saveStatus(
                                    this@PengajuanJadwalActivity,
                                    index,
                                    if (it.status == "selesai") 2 else 1
                                )
                            }
                        }

                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


            adapter = PertemuanAdapter(pertemuanList) { posisi, nama ->
                openForm(posisi, nama)
            }

            binding.rvPertemuan.layoutManager = LinearLayoutManager(this)
            binding.rvPertemuan.adapter = adapter
        }

        private fun openForm(posisi: Int, nama: String) {
            val intent = Intent(this, FormPengajuanActivity::class.java)
            intent.putExtra("pendaftaran_id", pendaftaranId)
            intent.putExtra("pertemuan_ke", posisi + 1)
            intent.putExtra("pertemuan_nama", nama)
            startActivityForResult(intent, REQ_PENGAJUAN)
        }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == REQ_PENGAJUAN && resultCode == Activity.RESULT_OK) {
                val posisi = data?.getIntExtra("pertemuan_index", -1) ?: return

                // Simpan status diproses (1)
                JadwalPrefs.saveStatus(this, posisi, 1)

                // Update tampilan list
                adapter.updateStatus(posisi, 1)
            }
        }
    }
