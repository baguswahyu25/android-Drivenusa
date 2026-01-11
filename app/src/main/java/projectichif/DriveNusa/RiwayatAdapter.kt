package projectichif.DriveNusa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import projectichif.DriveNusa.R
import projectichif.DriveNusa.api.RiwayatModel
import java.text.NumberFormat

class RiwayatAdapter(
    private val list: List<RiwayatModel>,
    private val onPendingClick: (RiwayatModel) -> Unit,
    private val onDetailClick: (RiwayatModel) -> Unit
) : RecyclerView.Adapter<RiwayatAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMobil: ImageView = itemView.findViewById(R.id.imgMobil)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvPaket: TextView = itemView.findViewById(R.id.tvPaket)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat_pemesanan, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = list[position]

        // Image
        Glide.with(holder.itemView.context)
            .load(data.image)
            .placeholder(R.drawable.img_placeholder)
            .into(holder.imgMobil)

        // Text
        holder.tvJudul.text = data.mobil
        holder.tvPaket.text = data.paket
        holder.tvHarga.text =
            "Rp ${NumberFormat.getInstance().format(data.harga)}"

        // STATUS
        val status = data.status.uppercase()
        holder.tvStatus.text = status

        when (status) {
            "PAID", "SETTLEMENT", "CAPTURE" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_paid)
                holder.itemView.alpha = 1f
            }

            "PENDING", "DP", "CICILAN" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                holder.itemView.alpha = 1f
            }

            else -> { // FAILED / EXPIRE / CANCEL
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_failed)
                holder.itemView.alpha = 0.5f
            }
        }

        // CLICK BEHAVIOR
        holder.itemView.setOnClickListener {
            when (status) {
                "PAID", "SETTLEMENT", "CAPTURE" -> onDetailClick(data)
                "PENDING", "DP", "CICILAN" -> onPendingClick(data)
            }
        }
    }

    override fun getItemCount() = list.size
}
