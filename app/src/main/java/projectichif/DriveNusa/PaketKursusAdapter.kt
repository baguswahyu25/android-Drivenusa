package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import projectichif.DriveNusa.databinding.ItemPaketKursusBinding

// INTERFACE BARU: Untuk menangani klik tombol Pilih
interface OnPaketClickListener {
    fun onPilihClicked(paket: PaketKursus)
}

class PaketKursusAdapter(
    // Jadikan hanya satu list yang bisa diubah lewat updateData
    private var paketList: List<PaketKursus>,
    // listener boleh null (default null) agar pemanggil bisa melewatkannya jika perlu
    private val listener: OnPaketClickListener? = null
) : RecyclerView.Adapter<PaketKursusAdapter.PaketViewHolder>() {

    fun updateData(newList: List<PaketKursus>) {
        paketList = newList
        notifyDataSetChanged()
    }

    inner class PaketViewHolder(private val binding: ItemPaketKursusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(paket: PaketKursus) {
            binding.tvNamaPaket.text = paket.nama
            binding.tvHarga.text = paket.harga
            binding.imgPaketMobil.setImageResource(paket.imageResId)

            // Fungsionalitas Tombol Pilih — aman walau listener = null
            binding.btnPilih.setOnClickListener {
                listener?.onPilihClicked(paket)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaketViewHolder {
        val binding = ItemPaketKursusBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaketViewHolder, position: Int) {
        holder.bind(paketList[position])
    }

    override fun getItemCount(): Int {
        return paketList.size
    }
}
