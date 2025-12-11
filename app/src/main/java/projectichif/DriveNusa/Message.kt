package projectichif.DriveNusa

data class Message(
    val text: String,
    val time: String,
    val isSentByUser: Boolean, // true jika pesan dikirim pengguna (kanan), false jika diterima (kiri)
    val senderAvatarResId: Int? = null // Hanya perlu jika pesan diterima (kiri)
)