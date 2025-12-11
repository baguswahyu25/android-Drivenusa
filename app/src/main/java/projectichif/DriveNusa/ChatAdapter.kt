package projectichif.DriveNusa
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Interface untuk menangani klik pada item chat
interface ChatItemClickListener {
    fun onChatClicked(chatItem: ChatItem)
}

class ChatAdapter(
    private val chats: List<ChatItem>,
    private val clickListener: ChatItemClickListener // <--- Tambahkan Click Listener
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        val senderName: TextView = itemView.findViewById(R.id.tv_sender_name)
        val lastMessage: TextView = itemView.findViewById(R.id.tv_last_message)
        val time: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = chats[position]
        holder.avatar.setImageResource(item.avatarResId)
        holder.senderName.text = item.senderName
        holder.lastMessage.text = item.lastMessage
        holder.time.text = item.time

        // === TAMBAHKAN ONCLICK LISTENER ===
        holder.itemView.setOnClickListener {
            clickListener.onChatClicked(item)
        }
        // ==================================

        // Opsional: atur warna atau style berdasarkan isOfficial jika diperlukan
    }

    override fun getItemCount() = chats.size
}