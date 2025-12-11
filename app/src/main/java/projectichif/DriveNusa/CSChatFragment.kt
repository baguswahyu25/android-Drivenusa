package projectichif.DriveNusa.ui.cs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import projectichif.DriveNusa.R
import projectichif.DriveNusa.adapter.CsChatAdapter
import projectichif.DriveNusa.databinding.FragmentCsChatBinding
import projectichif.DriveNusa.model.MessageModel
import java.text.SimpleDateFormat
import java.util.*

class CsChatFragment : Fragment() {

    private var _binding: FragmentCsChatBinding? = null
    private val binding get() = _binding!!

    private val messages = mutableListOf<MessageModel>()
    private lateinit var adapter: CsChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCsChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ========== SEMBUNYIKAN BOTTOM NAVIGATION ==========
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE

        // Setup RecyclerView
        adapter = CsChatAdapter(messages)
        binding.recyclerViewCsChat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCsChat.adapter = adapter

        // Pesan awal dari CS
        messages.add(
            MessageModel(
                sender = "CS",
                message = "Hi! Terima kasih sudah menggunakan aplikasi kami. Ada yang bisa dibantu?",
                time = getCurrentTime()
            )
        )
        adapter.notifyItemInserted(messages.size - 1)

        // Tombol kirim
        binding.btnSend.setOnClickListener {
            sendUserMessage()
        }

        // Tombol back
        binding.btnBackCs.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun sendUserMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        messages.add(
            MessageModel(
                sender = "You",
                message = text,
                time = getCurrentTime()
            )
        )

        binding.etMessage.text.clear()
        adapter.notifyItemInserted(messages.size - 1)
        binding.recyclerViewCsChat.scrollToPosition(messages.size - 1)
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // === KEMBALIKAN BOTTOM NAV SAAT KELUAR CHAT ===
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE

        _binding = null
    }
}
