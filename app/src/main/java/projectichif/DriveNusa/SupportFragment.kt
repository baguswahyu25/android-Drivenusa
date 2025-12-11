package projectichif.DriveNusa.ui.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import projectichif.DriveNusa.R
import projectichif.DriveNusa.databinding.FragmentSupportBinding

class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    private var ratingSelected = 0
    private lateinit var stars: List<ImageView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stars = listOf(
            binding.star1,
            binding.star2,
            binding.star3,
            binding.star4,
            binding.star5
        )

        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                ratingSelected = index + 1
                updateStarsUI()
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (ratingSelected > 0) {
                openThanksFragment()
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateStarsUI() {
        for (i in stars.indices) {
            if (i < ratingSelected) {
                stars[i].setImageResource(R.drawable.ic_star_filled)
            } else {
                stars[i].setImageResource(R.drawable.ic_star_outline)
            }
        }
    }

    // ⬇️ Fungsi pindah fragment manual TANPA Navigation Graph
    private fun openThanksFragment() {
        val fragment = ThanksFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
