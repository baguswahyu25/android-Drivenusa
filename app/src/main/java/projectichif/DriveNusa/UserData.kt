package projectichif.DriveNusa

data class UserData(
    val id: Int?,
    val name: String?,
    val email: String?,
    val phone: String? = null,
    val avatar_url: String? = null,
    val email_verified_at: String? = null
)
