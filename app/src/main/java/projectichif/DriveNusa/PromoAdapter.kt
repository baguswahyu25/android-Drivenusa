package projectichif.DriveNusa

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PromoAdapter(
    private val promos: MutableList<PromoItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = true

    companion object {
        private const val TYPE_SHIMMER = 0
        private const val TYPE_DATA = 1
    }

    fun showLoading() {
        isLoading = true
        notifyDataSetChanged()
    }

    fun submitData(newData: List<PromoItem>) {
        isLoading = false
        promos.clear()
        promos.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) TYPE_SHIMMER else TYPE_DATA
    }

    override fun getItemCount(): Int {
        return if (isLoading) 5 else promos.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_SHIMMER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_promo_shimmer, parent, false)
            object : RecyclerView.ViewHolder(view) {}
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_promo, parent, false)
            PromoVH(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PromoVH) {
            val item = promos[position]

            holder.title.text = item.title
            holder.subtitle.text = item.subtitle ?: "-"

            Glide.with(holder.itemView)
                .load(item.imageUrl)
                .override(900, 350)
                .timeout(20_000)
                .centerCrop()
                .placeholder(R.drawable.placeholder_promo)
                .error(R.drawable.placeholder_promo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image)




            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, PromoDetailActivity::class.java)
                intent.putExtra("PROMO_ID", item.id)
                intent.putExtra("TITLE", item.title)
                intent.putExtra("DESC", item.description)
                intent.putExtra("IMAGE", item.imageUrl)
                intent.putExtra("EXPIRED", item.expiredAt)
                holder.itemView.context.startActivity(intent)
            }
        }
    }



    inner class PromoVH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvTitle)
        val subtitle: TextView = v.findViewById(R.id.tvSubtitle)
        val image: ImageView = v.findViewById(R.id.imgPromo)
    }

}



