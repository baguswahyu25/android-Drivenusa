package projectichif.DriveNusa.ui.cs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import projectichif.DriveNusa.R
import projectichif.DriveNusa.adapter.CsChatAdapter
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.FragmentCsChatBinding
import java.text.SimpleDateFormat
import java.util.*
import projectichif.DriveNusa.api.MessageModel




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

        // sembunyikan bottom nav
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE

        adapter = CsChatAdapter(messages)
        binding.recyclerViewCsChat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCsChat.adapter = adapter

        // greeting awal
        messages.add(
            MessageModel(
                sender = "CS Bot",
                message = "Halo 👋 Ada yang bisa kami bantu?",
                time = getCurrentTime()
            )
        )
        adapter.notifyItemInserted(messages.size - 1)

        binding.btnSend.setOnClickListener { sendMessage() }
        binding.btnBackCs.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        // tampilkan pesan user
        messages.add(MessageModel("You", text, getCurrentTime()))
        adapter.notifyItemInserted(messages.size - 1)
        binding.recyclerViewCsChat.scrollToPosition(messages.size - 1)
        binding.etMessage.text.clear()

        showTyping()

        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                AuthRepository.sendBotMessage(requireContext(), text)
            }

            hideTyping()

            val replyText = response?.reply ?: "Koneksi bermasalah."
            val senderName =
                if (response?.source == "cs") "Customer Service" else "CS Bot"

            messages.add(
                MessageModel(
                    sender = senderName,
                    message = replyText,
                    time = getCurrentTime()
                )
            )

            adapter.notifyItemInserted(messages.size - 1)
            binding.recyclerViewCsChat.scrollToPosition(messages.size - 1)
        }
    }

    private fun showTyping() {
        messages.add(
            MessageModel(
                sender = "CS Bot",
                message = "Sedang mengetik...",
                time = getCurrentTime(),
                isTyping = true
            )
        )
        adapter.notifyItemInserted(messages.size - 1)
    }

    private fun hideTyping() {
        if (messages.lastOrNull()?.isTyping == true) {
            messages.removeAt(messages.size - 1)
            adapter.notifyItemRemoved(messages.size)
        }
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE
        _binding = null
    }
}
