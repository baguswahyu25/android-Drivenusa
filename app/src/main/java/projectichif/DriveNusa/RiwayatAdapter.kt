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
    private val onClick: (RiwayatModel) -> Unit
) : RecyclerView.Adapter<RiwayatAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMobil: ImageView = itemView.findViewById(R.id.imgMobil)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvPaket: TextView = itemView.findViewById(R.id.tvPaket)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat_pemesanan, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = list[position]

        Glide.with(holder.itemView.context)
            .load(data.image)
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_placeholder)
            .into(holder.imgMobil)

        holder.tvJudul.text = data.judul
        holder.tvPaket.text = data.paket
        holder.tvHarga.text = "Rp ${NumberFormat.getInstance().format(data.harga)}"

        holder.tvTanggal.text = data.tanggal.replace(" ", "\n")

        holder.itemView.setOnClickListener { onClick(data) }
        holder.tvStatus.text = data.status

        if (data.status == "PAID") {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_paid)
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
        }

    }

    override fun getItemCount(): Int = list.size
}

