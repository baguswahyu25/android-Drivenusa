package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.PaketKursus
import projectichif.DriveNusa.api.AuthApi
import projectichif.DriveNusa.api.toPaketKursus
import projectichif.DriveNusa.databinding.FragmentHomeBinding
import projectichif.DriveNusa.utils.JadwalPrefs
import androidx.fragment.app.activityViewModels

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaketKursusViewModel by activityViewModels()
    private lateinit var adapter: PaketKursusAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    private var sliderItems = listOf(
        ImageSlider(R.drawable.img_slider_1),
        ImageSlider(R.drawable.img_slider_2),
        ImageSlider(R.drawable.img_slider_3)
    )

    private lateinit var sliderAdapter: ImageSliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Search click
        binding.cardSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            intent.putExtra("query", "")
            startActivity(intent)
        }
        binding.cardPertemuan.setOnClickListener {
            startActivity(Intent(requireContext(), PengajuanJadwalActivity::class.java))
        }
        // Slider setup
        sliderAdapter = ImageSliderAdapter(sliderItems)
        binding.viewPagerSlider.adapter = sliderAdapter
        binding.viewPagerSlider.offscreenPageLimit = 1
        startAutoSlide()

        adapter = PaketKursusAdapter(emptyList(), object : OnPaketClickListener {
            override fun onPilihClicked(paket: PaketKursus) {
                startActivity(
                    Intent(requireContext(), SyaratActivity::class.java)
                        .putExtra(SyaratActivity.EXTRA_PAKET_NAMA, paket.nama)
                )
            }
        })

        binding.rvPaketKursus.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPaketKursus.adapter = ShimmerAdapter(5)

        // ✅ OBSERVE DATA DARI VIEWMODEL
        viewModel.paketKursus.observe(viewLifecycleOwner) { list ->
            binding.rvPaketKursus.adapter = adapter
            adapter.updateData(list)
        }

        // ✅ LOAD SEKALI SAJA (AMAN)
        viewModel.loadPaketKursus()
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

    override fun onResume() {
        super.onResume()
        refreshJadwal()
    }

    private fun refreshJadwal() {
        val statusList = JadwalPrefs.getStatusList(requireContext())
        val nextIndex = statusList.indexOfFirst { it == 0 || it == 1 }

        if (nextIndex != -1) {
            binding.tvPertemuan.text = "Pertemuan ${nextIndex + 1}"
            when (statusList[nextIndex]) {
                0 -> {
                    binding.tvStatus.text = "Belum diajukan"
                    binding.tvStatus.setTextColor(android.graphics.Color.BLACK)
                }
                1 -> {
                    binding.tvStatus.text = "Diproses..."
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                }
            }
        } else {
            binding.tvPertemuan.text = "Semua pertemuan selesai"
            binding.tvStatus.text = "Selesai"
            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}

