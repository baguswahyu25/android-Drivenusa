package projectichif.DriveNusa

import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.api.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatDetailFragment : Fragment() {

    companion object {
        private const val ARG_SENDER_NAME = "sender_name"

        fun newInstance(senderName: String): ChatDetailFragment {
            return ChatDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SENDER_NAME, senderName)
                }
            }
        }
    }

    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageView
    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private val messagesList = mutableListOf<Message>()

    private val handler = Handler(Looper.getMainLooper())
    private var roomId: Int = 1 // TODO: sesuaikan dengan backend / notifikasi

    override fun onResume() {
        super.onResume()
        startPollingChat()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etMessage = view.findViewById(R.id.et_message)
        btnSend = view.findViewById(R.id.btn_send_or_attach)
        val tvOfficialName = view.findViewById<TextView>(R.id.tv_official_name)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_messages)

        tvOfficialName.text =
            arguments?.getString(ARG_SENDER_NAME) ?: "Official Drive Nusa"

        // Adapter sekarang menerima MutableList
        chatDetailAdapter = ChatDetailAdapter(messagesList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatDetailAdapter

        btnSend.setImageResource(R.drawable.ic_attach)

        // Ganti icon attach/send sesuai input
        etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnSend.setImageResource(
                    if (s.isNullOrEmpty()) R.drawable.ic_attach
                    else R.drawable.ic_send
                )
            }
        })

        // Tombol kirim pesan
        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            lifecycleScope.launch {
                val success = AuthRepository.sendChat(
                    roomId = roomId,
                    message = text
                )

                if (success) {
                    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    messagesList.add(Message(text, time, true))
                    chatDetailAdapter.notifyItemInserted(messagesList.size - 1)
                    recyclerView.scrollToPosition(messagesList.size - 1)
                    etMessage.text.clear()
                } else {
                    // Tambahkan log jika gagal
                    println("DEBUG: Gagal mengirim pesan")
                }
            }
        }

        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun startPollingChat() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                loadMessages()
                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            val apiMessages = AuthRepository.getChats(requireContext(), roomId)
            val currentUserId = UserLocal.getUser(requireContext())?.id ?: return@launch

            apiMessages?.let { list ->
                messagesList.clear()
                val uiMessages = list.map { it.toUiMessage(currentUserId) }
                messagesList.addAll(uiMessages)
                chatDetailAdapter.notifyDataSetChanged()
            }
        }
    }


    // Mapper dari API → UI
    private fun ChatMessage.toUiMessage(currentUserId: Int): Message {
        return Message(
            text = message,
            time = createdAt.substring(11, 16),
            isSentByUser = senderId == currentUserId
        )
    }

    override fun onDetach() {
        super.onDetach()
        (activity as? HomeActivity)?.setBottomNavigationVisibility(true)
    }
}
