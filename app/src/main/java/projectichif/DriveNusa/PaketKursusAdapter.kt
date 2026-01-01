package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import projectichif.DriveNusa.api.PaketKursus
import projectichif.DriveNusa.databinding.ItemPaketKursusBinding

interface OnPaketClickListener {
    fun onPilihClicked(paket: PaketKursus)
}

class PaketKursusAdapter(
    private var paketList: List<PaketKursus>,
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

            // Load image dari URL Laravel storage
            Glide.with(binding.imgPaketMobil.context)
                .load(paket.image)
                .centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.img_placeholder))
                .into(binding.imgPaketMobil)

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

    override fun getItemCount(): Int = paketList.size
}
