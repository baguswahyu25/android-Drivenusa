package projectichif.DriveNusa.api

data class MessageModel(
    val sender: String,
    val message: String,
    val time: String,
    val isTyping: Boolean = false
)
