package projectichif.DriveNusa

data class NotificationItem(
    val iconResId: Int,
    val title: String,
    val subtitle: String,
    val type: NotificationType,
    val payload: Any? = null
)

enum class NotificationType {
    PENGAJUAN_JADWAL,
    PROMO
}
