package projectichif.DriveNusa.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import projectichif.DriveNusa.R

fun ImageView.loadProfile(source: Any?) {
    val placeholder = R.drawable.ic_user

    if (source == null) {
        setImageResource(placeholder)
        return
    }

    Glide.with(this)
        .load(source)
        .placeholder(placeholder)
        .error(placeholder)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(false)
        .dontAnimate()
        .into(this)
}

