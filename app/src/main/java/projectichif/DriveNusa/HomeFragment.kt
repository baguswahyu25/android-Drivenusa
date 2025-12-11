package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import projectichif.DriveNusa.databinding.FragmentHomeBinding
import projectichif.DriveNusa.utils.JadwalPrefs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sliderAdapter: ImageSliderAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    private val sliderItems = listOf(
        ImageSlider(R.drawable.img_slider_1),
        ImageSlider(R.drawable.img_slider_2),
        ImageSlider(R.drawable.img_slider_3)
    )

    private val paketKursusList = listOf(
        PaketKursus("Paket Manual", "Rp 1.150.000", R.drawable.img_paket_manual),
        PaketKursus("Paket Automatic", "Rp 1.250.000", R.drawable.img_paket_automatic),
        PaketKursus("Paket Manual Sim", "Rp 2.100.000", R.drawable.img_paket_manual_sim_baru),
        PaketKursus("Paket Automatic Sim", "Rp 2.200.000", R.drawable.img_paket_automatic_sim)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Search
        binding.cardSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            intent.putExtra("query", "")
            startActivity(intent)
        }

        // Slider
        sliderAdapter = ImageSliderAdapter(sliderItems)
        binding.viewPagerSlider.adapter = sliderAdapter
        binding.viewPagerSlider.offscreenPageLimit = 1
        startAutoSlide()

        // Paket Kursus
        binding.rvPaketKursus.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPaketKursus.adapter = PaketKursusAdapter(
            paketKursusList,
            object : OnPaketClickListener {
                override fun onPilihClicked(paket: PaketKursus) {
                    val intent = Intent(requireContext(), SyaratActivity::class.java)
                    intent.putExtra(SyaratActivity.EXTRA_PAKET_NAMA, paket.nama)
                    startActivity(intent)
                }
            }
        )
        binding.cardPertemuan.setOnClickListener {
            startActivity(Intent(requireContext(), PengajuanJadwalActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshJadwal()
    }

    /** ==============================
     *  FUNGSI INTI UNTUK UPDATE UI
     *  ============================== */
    private fun refreshJadwal() {
        val statusList = JadwalPrefs.getStatusList(requireContext())

        // Cari pertemuan pertama yang belum diajukan
        val nextIndex = statusList.indexOfFirst { it == 0 || it == 1 }

        if (nextIndex != -1) {
            binding.tvPertemuan.text = "Pertemuan ${nextIndex + 1}"

            when (statusList[nextIndex]) {
                0 -> { // Belum diajukan
                    binding.tvStatus.text = "Belum diajukan"
                    binding.tvStatus.setTextColor(android.graphics.Color.BLACK)
                }
                1 -> { // Diproses
                    binding.tvStatus.text = "Diproses..."
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                }
            }
        } else {
            // Semua selesai
            binding.tvPertemuan.text = "Semua pertemuan selesai"
            binding.tvStatus.text = "Selesai"
            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
        }
    }



    private fun startAutoSlide() {
        val runnable = object : Runnable {
            override fun run() {
                if (sliderItems.isNotEmpty()) {
                    currentPage = (currentPage + 1) % sliderItems.size
                    binding.viewPagerSlider.setCurrentItem(currentPage, true)
                    handler.postDelayed(this, 3000)
                }
            }
        }
        handler.postDelayed(runnable, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
