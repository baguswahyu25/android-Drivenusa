package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import projectichif.DriveNusa.api.PaketKursus
import projectichif.DriveNusa.databinding.ItemPaketKursusBinding
import projectichif.DriveNusa.databinding.ItemPaketKursusHomeBinding

interface OnPaketClickListener {
    fun onPilihClicked(paket: PaketKursus)
}

class PaketKursusAdapter(
    private var paketList: List<PaketKursus>,
    private val isHome: Boolean,
    private val listener: OnPaketClickListener? = null
) : RecyclerView.Adapter<PaketKursusAdapter.PaketViewHolder>() {

    fun updateData(newList: List<PaketKursus>) {
        paketList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaketViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = if (isHome) {
            ItemPaketKursusHomeBinding.inflate(inflater, parent, false)
        } else {
            ItemPaketKursusBinding.inflate(inflater, parent, false)
        }

        return PaketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaketViewHolder, position: Int) {
        holder.bind(paketList[position])
    }

    override fun getItemCount(): Int = paketList.size

    inner class PaketViewHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(paket: PaketKursus) {
            when (binding) {
                is ItemPaketKursusHomeBinding -> bindHome(binding, paket)
                is ItemPaketKursusBinding -> bindGrid(binding, paket)
            }
        }

        private fun bindHome(
            binding: ItemPaketKursusHomeBinding,
            paket: PaketKursus
        ) {
            binding.tvNamaPaket.text = paket.nama
            binding.tvHarga.text = paket.harga

            Glide.with(binding.imgPaketMobil.context)
                .load(paket.image)
                .placeholder(R.drawable.img_placeholder)
                .centerCrop()
                .into(binding.imgPaketMobil)

            binding.btnPilih.setOnClickListener {
                listener?.onPilihClicked(paket)
            }
        }

        private fun bindGrid(
            binding: ItemPaketKursusBinding,
            paket: PaketKursus
        ) {
            binding.tvNamaPaket.text = paket.nama
            binding.tvHarga.text = paket.harga

            Glide.with(binding.imgPaketMobil.context)
                .load(paket.image)
                .placeholder(R.drawable.img_placeholder)
                .centerCrop()
                .into(binding.imgPaketMobil)

            binding.btnPilih.setOnClickListener {
                listener?.onPilihClicked(paket)
            }
        }
    }
}

