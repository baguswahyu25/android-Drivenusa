package projectichif.DriveNusa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import projectichif.DriveNusa.databinding.ItemSliderBinding

class ImageSliderAdapter(
    private val sliderItems: List<ImageSlider>
) : RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(private val binding: ItemSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageSlider) {
            binding.imageSlider.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(sliderItems[position])
    }

    override fun getItemCount(): Int = sliderItems.size
}
