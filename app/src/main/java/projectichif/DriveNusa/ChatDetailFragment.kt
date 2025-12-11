package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
// Hapus import yang tidak perlu: android.content.Context dan android.view.inputmethod.InputMethodManager

class ChatDetailFragment : Fragment() {

    // ... (Companion object dan newInstance - TIDAK ADA PERUBAHAN) ...
    companion object {
        private const val ARG_SENDER_NAME = "sender_name"

        fun newInstance(senderName: String): ChatDetailFragment {
            val fragment = ChatDetailFragment()
            val args = Bundle()
            args.putString(ARG_SENDER_NAME, senderName)
            fragment.arguments = args
            return fragment
        }
    }

    // Variabel untuk menampung referensi View
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageView // btnSend sekarang adalah Tombol Kirim/Lampiran
    // HAPUS: private lateinit var btnEmoji: ImageView
    // HAPUS: private lateinit var btnMic: ImageView
    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private val messagesList = mutableListOf<Message>()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data

            if (imageUri != null) {
                Toast.makeText(context, "Gambar berhasil dipilih! URI: ${imageUri.lastPathSegment}", Toast.LENGTH_LONG).show()
                // TODO: Di sini Anda harus menambahkan Message dengan gambar ke messagesList
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Pastikan nama layout sudah benar: R.layout.fragment_chat_detail
        return inflater.inflate(R.layout.fragment_chat_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // === 1. INICIALISASI VIEW (Diperbarui) ===
        etMessage = view.findViewById(R.id.et_message)
        btnSend = view.findViewById(R.id.btn_send_or_attach) // GANTI ID di XML!

        val tvOfficialName = view.findViewById<TextView>(R.id.tv_official_name)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_messages)

        // ... (Pengaturan nama dan Adapter - TIDAK ADA PERUBAHAN) ...
        val senderName = arguments?.getString(ARG_SENDER_NAME) ?: "Official Drive nusa"
        tvOfficialName.text = senderName

        messagesList.addAll(listOf(
            Message("Hai!", "10:30", true),
            Message("Silahkan jangan ragu untuk bertanya", "10:31", false, R.drawable.ic_nusa),
            Message("Berapa jumlah pertemuan dalam kursusnya?", "10:34", true),
            Message("Kursus ini terdiri dari 14 pertemuan", "10:36", false, R.drawable.ic_nusa),
        ))

        chatDetailAdapter = ChatDetailAdapter(messagesList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chatDetailAdapter
        recyclerView.scrollToPosition(messagesList.size - 1)


        // === 2. LOGIKA TOMBOL KIRIM (ATTACH -> SEND) ===
        // Atur ikon default (Lampiran)
        btnSend.setImageResource(R.drawable.ic_attach)

        // TextWatcher untuk mengganti ikon Lampiran/Kirim
        etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Gunakan ikon kirim jika ada teks
                if (s?.length ?: 0 > 0) {
                    btnSend.setImageResource(R.drawable.ic_send)
                } else {
                    btnSend.setImageResource(R.drawable.ic_attach)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Listener Klik Tombol Kirim/Attach
        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString().trim()

            if (messageText.isNotEmpty()) {
                // LOGIKA KIRIM PESAN TEXT
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                messagesList.add(Message(messageText, currentTime, true))
                chatDetailAdapter.notifyItemInserted(messagesList.size - 1)
                recyclerView.scrollToPosition(messagesList.size - 1)
                etMessage.text.clear()
            } else {
                // LOGIKA TOMBOL ATTACH (MEMBUKA GALERI)
                openGallery()
            }
        }

        // === HAPUS: 4. LOGIKA TOMBOL MIC & EMOJI BARU/DIPERBAIKI ===
        // FUNGSI INI SEKARANG DITANGANI OLEH KEYBOARD SISTEM

        // === 3. HANDLE TOMBOL KEMBALI ===
        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    override fun onDetach() {
        super.onDetach()
        // Tampilkan kembali Bottom Navigation Bar
        (activity as? HomeActivity)?.setBottomNavigationVisibility(true)
    }
}