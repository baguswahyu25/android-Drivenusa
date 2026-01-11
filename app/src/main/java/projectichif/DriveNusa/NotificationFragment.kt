package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

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
                iconResId = R.drawable.ic_promo_notif,
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

                lifecycleScope.launch {
                    try {
                        val response = ApiClient.authApi.getPendaftaranAktif()

                        if (response.isSuccessful && response.body() != null) {
                            val intent = Intent(
                                requireContext(),
                                PengajuanJadwalActivity::class.java
                            )
                            intent.putExtra(
                                "pendaftaran_id",
                                response.body()!!.id
                            )
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Belum ada pendaftaran aktif",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Gagal membuka pengajuan jadwal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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
