package projectichif.DriveNusa

data class ChatItem(
    val avatarResId: Int, // Resource ID untuk gambar profil
    val senderName: String,
    val lastMessage: String,
    val time: String,
    val isOfficial: Boolean // Untuk membedakan Official Dhuha atau Juru Kemudi
)