package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationFragment : Fragment(), NotificationClickListener {

    private val notificationList = mutableListOf<NotificationItem>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_notifications)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NotificationAdapter(notificationList, this)
        recyclerView.adapter = adapter

        loadDummyNotifications()
    }

    private fun loadDummyNotifications() {
        val items = listOf(
            NotificationItem(
                iconResId = R.drawable.ic_calender,
                title = "Pengajuan Jadwal",
                subtitle = "Ajukan jadwal pertemuan Anda",
                type = NotificationType.PENGAJUAN_JADWAL
            ),
            NotificationItem(
                iconResId = R.drawable.ic_chat,
                title = "Chat",
                subtitle = "ada chat terbaru",
                type = NotificationType.CHAT,
                payload = "" // nama sender
            ),
            NotificationItem(
                iconResId = R.drawable.ic_discount,
                title = "Promo Terbaru",
                subtitle = "Cek promo menarik hari ini",
                type = NotificationType.PROMO,
                payload = 123 // id promo contoh
            )
        )

        adapter.setNotifications(items)
    }

    override fun onNotificationClicked(item: NotificationItem) {
        when (item.type) {
            NotificationType.PENGAJUAN_JADWAL -> {
                val intent = Intent(requireContext(), PengajuanJadwalActivity::class.java)
                startActivity(intent)
            }
            NotificationType.CHAT -> {
                val chatListFragment = ChatListFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, chatListFragment)
                    .addToBackStack(null)
                    .commit()
            }

            NotificationType.PROMO -> {
                val intent = Intent(requireContext(), PromoActivity::class.java)
                intent.putExtra("FROM_NOTIF", true)
                intent.putExtra("PROMO_ID", item.payload as? Int ?: -1)
                startActivity(intent)
            }
        }
    }
}
