package projectichif.DriveNusa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Fragment mengimplementasikan interface klik
class ChatListFragment : Fragment(), ChatItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menggunakan layout fragment_chat_list.xml
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_chat_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Data Dummy (hanya official Drivenusa)
        val chatList = listOf(
            ChatItem(R.drawable.ic_nusa, "official Drivenusa", "Kursus ini terdiri dari 14 pertemuan", "10:38", true)
        )

        // Set adapter, kirim 'this' (Fragment) sebagai listener
        recyclerView.adapter = ChatAdapter(chatList, this)

        // Handle Tombol Kembali (btn_back)
        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


    // === IMPLEMENTASI DARI CHAT ITEM CLICK LISTENER ===
    override fun onChatClicked(chatItem: ChatItem) {
        // 1. Sembunyikan Bottom Navigation Bar sebelum navigasi
        (activity as? HomeActivity)?.setBottomNavigationVisibility(false)

        // 2. Pindah ke Chat Detail Fragment, kirim nama pengirim sebagai argumen
        val chatDetailFragment = ChatDetailFragment.newInstance(chatItem.senderName)

        parentFragmentManager.beginTransaction()
            // MEMPERBAIKI ID CONTAINER: Gunakan ID yang benar dari HomeActivity
            .replace(R.id.fragment_container, chatDetailFragment)
            .addToBackStack(null)
            .commit()
    }
}