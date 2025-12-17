package projectichif.DriveNusa
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatDetailAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<ChatDetailAdapter.MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1 // Pesan dikirim pengguna (kanan)
        private const val VIEW_TYPE_RECEIVED = 2 // Pesan diterima (kiri)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByUser) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_sent, parent, false)
                MessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_received, parent, false)
                MessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount() = messages.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.tv_message_text)
        private val time: TextView = itemView.findViewById(R.id.tv_time)
        private val avatar: ImageView? = itemView.findViewById(R.id.iv_avatar)

        fun bind(message: Message) {
            messageText.text = message.text
            time.text = message.time

            // Atur avatar hanya jika pesan DITERIMA
            if (getItemViewType(adapterPosition) == VIEW_TYPE_RECEIVED && avatar != null && message.senderAvatarResId != null) {
                avatar.setImageResource(message.senderAvatarResId)
            }
        }
    }
}