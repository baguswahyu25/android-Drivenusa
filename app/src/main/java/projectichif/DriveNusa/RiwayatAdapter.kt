package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat_pemesanan, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = list[position]

        holder.imgMobil.setImageResource(data.gambar)
        holder.tvJudul.text = data.judul
        holder.tvPaket.text = data.paket
        holder.tvHarga.text = data.harga
        holder.tvTanggal.text = data.tanggal.replace(", ", "\n")

        holder.itemView.setOnClickListener {
            onClick(data)
        }
    }

    override fun getItemCount(): Int = list.size
}
