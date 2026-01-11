package projectichif.DriveNusa

import android.content.Intent
import android.graphics.BitmapFactory
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaketKursusViewModel by activityViewModels()
    private lateinit var adapter: PaketKursusAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    private var sliderItems = listOf(
        ImageSlider(R.drawable.img_instruktur_1),
        ImageSlider(R.drawable.img_instruktur_2),
        ImageSlider(R.drawable.img_instruktur_3),
        ImageSlider(R.drawable.img_instruktur_4),
        ImageSlider(R.drawable.img_instruktur_5)
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
//        binding.cardPertemuan.setOnClickListener {
//            startActivity(Intent(requireContext(), PengajuanJadwalActivity::class.java))
//        }
        // Slider setup
        sliderAdapter = ImageSliderAdapter(sliderItems)
        binding.viewPagerSlider.adapter = sliderAdapter
        binding.viewPagerSlider.offscreenPageLimit = 1
        startAutoSlide()

        adapter = PaketKursusAdapter(
            paketList = emptyList(),
            isHome = true,
            listener = object : OnPaketClickListener {
                override fun onPilihClicked(paket: PaketKursus) {
                    val intent = Intent(requireContext(), SyaratActivity::class.java)
                    intent.putExtra(SyaratActivity.EXTRA_PAKET_NAMA, paket.nama)
                    startActivity(intent)
                }

            }
        )



        binding.rvPaketKursus.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvPaketKursus.setHasFixedSize(true)
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
    override fun onStart() {
        super.onStart()
        loadProfilePhoto()
    }

    private fun loadProfilePhoto() {
        val ctx = requireContext()
        val user = UserLocal.getUser(ctx) ?: return
        val userId = user.id ?: return

        // 1️⃣ local dulu
        val localPath = UserLocal.getProfilePhotoLocalPath(ctx, userId)
        if (!localPath.isNullOrEmpty() && File(localPath).exists()) {
            binding.profileImage.setImageBitmap(
                BitmapFactory.decodeFile(localPath)
            )
            return
        }

        // 2️⃣ server
        if (!user.profilePhotoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(user.profilePhotoUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_person)
        }
    }

    override fun onResume() {
        super.onResume()
//        refreshJadwal()
    }

//    private fun refreshJadwal() {
//        val statusList = JadwalPrefs.getStatusList(requireContext())
//        val nextIndex = statusList.indexOfFirst { it == 0 || it == 1 }
//
//        if (nextIndex != -1) {
//            binding.tvPertemuan.text = "Pertemuan ${nextIndex + 1}"
//            when (statusList[nextIndex]) {
//                0 -> {
//                    binding.tvStatus.text = "Belum diajukan"
//                    binding.tvStatus.setTextColor(android.graphics.Color.BLACK)
//                }
//                1 -> {
//                    binding.tvStatus.text = "Diproses..."
//                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
//                }
//            }
//        } else {
//            binding.tvPertemuan.text = "Semua pertemuan selesai"
//            binding.tvStatus.text = "Selesai"
//            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}

