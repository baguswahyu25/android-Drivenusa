package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import projectichif.DriveNusa.databinding.ItemSearchHistoryBinding

class SearchHistoryAdapter(
    private val historyList: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: ItemSearchHistoryBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.binding.tvHistoryText.text = item

        holder.binding.root.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = historyList.size
}
