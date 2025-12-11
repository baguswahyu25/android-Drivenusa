package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Fragment mengimplementasikan interface klik
class NotificationFragment : Fragment(), NotificationClickListener {

    private val notificationList = listOf(
        // Posisi 0
        NotificationItem(R.drawable.ic_calender, "hi user", "jangan lupa untuk melakukan pengajuan jadwal pertemuan"),
        // Posisi 1 (Pesan baru, yang akan mengarah ke chatting)
        NotificationItem(R.drawable.ic_person, "hi user", "ada pesan baru dari juru kemudi yang anda pilih"),
        // Posisi 2
        NotificationItem(R.drawable.ic_discount, "hi user", "lagi ada discount nih, yuk cek !!")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragment ini
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Di sini kita akan inisialisasi RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_notifications)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Set adapter ke RecyclerView, berikan 'this' (Fragment) sebagai listener
        recyclerView.adapter = NotificationAdapter(notificationList, this)
    }

    // === IMPLEMENTASI DARI NOTIFICATION CLICK LISTENER ===
    override fun onNotificationClicked(position: Int) {
        when (position) {
            0 -> { // Notifikasi "pengajuan jadwal"
                val intent = Intent(context, PengajuanJadwalActivity::class.java)
                startActivity(intent)
            }
            1 -> { // Notifikasi "ada pesan baru" -> ChatListFragment
                val chatListFragment = ChatListFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, chatListFragment)
                    .addToBackStack(null)
                    .commit()
            }
            else -> { // Notifikasi lain
                Toast.makeText(
                    context,
                    "Membuka detail notifikasi ${notificationList[position].subtitle}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}