package projectichif.DriveNusa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import projectichif.DriveNusa.R
import projectichif.DriveNusa.model.MessageModel

class CsChatAdapter(private val messages: List<MessageModel>) :
    RecyclerView.Adapter<CsChatAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSender: TextView = view.findViewById(R.id.tvSender)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cs_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messages[position]
        holder.tvSender.text = item.sender
        holder.tvMessage.text = item.message
        holder.tvTime.text = item.time
    }
}
