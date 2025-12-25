package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout

class ShimmerAdapter(private val itemCount: Int) : RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {

    class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paket_kursus_shimmer, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        val shimmerLayout = holder.itemView as ShimmerFrameLayout
        shimmerLayout.startShimmer()
    }

    override fun getItemCount(): Int = itemCount
}
