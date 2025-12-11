package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import projectichif.DriveNusa.databinding.ItemPertemuanBinding

data class PertemuanModel(
    val nama: String,
    var status: Int // 0 = belum, 1 = diproses, 2 = selesai
)

class PertemuanAdapter(
    private val list: MutableList<PertemuanModel>,
    private val onClick: (posisi: Int, nama: String) -> Unit
) : RecyclerView.Adapter<PertemuanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPertemuanBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPertemuanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.txtPertemuan.text = item.nama
        updateUI(holder, item.status)

        holder.binding.btnAjukan.setOnClickListener {
            onClick(position, item.nama)
        }
    }

    fun updateStatus(posisi: Int, status: Int) {
        list[posisi].status = status
        notifyItemChanged(posisi)
    }

    private fun updateUI(holder: ViewHolder, status: Int) {
        val b = holder.binding
        when (status) {
            0 -> { // Belum diajukan
                b.btnAjukan.visibility = View.VISIBLE
                b.txtProses.visibility = View.GONE
                b.icSelesai.visibility = View.GONE
            }

            1 -> { // Diproses
                b.btnAjukan.visibility = View.GONE
                b.txtProses.visibility = View.VISIBLE
                b.icSelesai.visibility = View.GONE
            }

            2 -> { // Selesai
                b.btnAjukan.visibility = View.GONE
                b.txtProses.visibility = View.GONE
                b.icSelesai.visibility = View.VISIBLE
            }
        }
    }
}
