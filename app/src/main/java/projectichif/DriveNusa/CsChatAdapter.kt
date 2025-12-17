package projectichif.DriveNusa.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import projectichif.DriveNusa.R
import projectichif.DriveNusa.api.MessageModel


class CsChatAdapter(private val messages: List<MessageModel>) :
    RecyclerView.Adapter<CsChatAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val bubbleContainer: View = view.findViewById(R.id.bubbleContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cs_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messages[position]

        holder.tvMessage.text = item.message
        holder.tvTime.text = item.time

        val layoutParams = holder.bubbleContainer.layoutParams as LinearLayout.LayoutParams
        if (item.sender == "You") {
            layoutParams.gravity = Gravity.END
            holder.bubbleContainer.setBackgroundResource(R.drawable.bg_chat_user)
        } else {
            layoutParams.gravity = Gravity.START
            holder.bubbleContainer.setBackgroundResource(R.drawable.bg_chat_bot)
        }

        holder.bubbleContainer.layoutParams = layoutParams

    }
}

