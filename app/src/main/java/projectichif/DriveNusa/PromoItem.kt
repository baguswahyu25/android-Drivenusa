package projectichif.DriveNusa

import com.google.gson.annotations.SerializedName

data class PromoItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("subtitle")
    val subtitle: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("image_url")
    val imageUrl: String?,   // ✅ GANTI

    @SerializedName("expired_at")
    val expiredAt: String?
)

object PromoCache {
    var promos: List<PromoItem>? = null
}




