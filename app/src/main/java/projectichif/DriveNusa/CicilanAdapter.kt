package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import projectichif.DriveNusa.api.CicilanItem

class CicilanAdapter(
    private val items: List<CicilanItem>,
    private val onBayarClick: (CicilanItem) -> Unit
) : RecyclerView.Adapter<CicilanAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tvLabel)
        val amount: TextView = view.findViewById(R.id.tvAmount)
        val status: TextView = view.findViewById(R.id.tvStatus)
        val btnBayar: Button = view.findViewById(R.id.btnBayar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cicilan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.label.text = item.label
        holder.amount.text = "Rp ${item.amount}"
        holder.status.text = item.status.uppercase()

        val isFirstPending =
            item.status == "pending" &&
                    items.indexOfFirst { it.status == "pending" } == position

        holder.btnBayar.visibility =
            if (isFirstPending) View.VISIBLE else View.GONE

        holder.btnBayar.setOnClickListener {
            onBayarClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
