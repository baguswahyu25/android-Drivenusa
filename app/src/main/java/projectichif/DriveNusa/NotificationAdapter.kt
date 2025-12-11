package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Definisikan Interface Click Listener
interface NotificationClickListener {
    fun onNotificationClicked(position: Int)
}

// Modifikasi konstruktor adapter untuk menerima listener
class NotificationAdapter(
    private val notifications: List<NotificationItem>,
    private val clickListener: NotificationClickListener // <--- TAMBAHAN
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iv_icon)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val subtitle: TextView = itemView.findViewById(R.id.tv_subtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notifications[position]
        holder.icon.setImageResource(item.iconResId)
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle

        // Menambahkan listener klik ke item view
        holder.itemView.setOnClickListener {
            clickListener.onNotificationClicked(position)
        }
    }

    override fun getItemCount() = notifications.size
}