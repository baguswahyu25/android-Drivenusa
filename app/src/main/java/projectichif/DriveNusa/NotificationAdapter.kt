package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface NotificationClickListener {
    fun onNotificationClicked(item: NotificationItem)
}

class NotificationAdapter(
    private val notificationList: MutableList<NotificationItem>,
    private val listener: NotificationClickListener
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tv_subtitle)

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onNotificationClicked(notificationList[pos])
                }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = notificationList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notificationList[position]
        holder.ivIcon.setImageResource(item.iconResId)
        holder.tvTitle.text = item.title
        holder.tvSubtitle.text = item.subtitle
    }

    fun setNotifications(list: List<NotificationItem>) {
        notificationList.clear()
        notificationList.addAll(list)
        notifyDataSetChanged()
    }
}
