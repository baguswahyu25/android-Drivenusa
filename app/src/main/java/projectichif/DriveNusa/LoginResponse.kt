package projectichif.DriveNusa.api

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: UserData?,
    val message: String? = null
)
