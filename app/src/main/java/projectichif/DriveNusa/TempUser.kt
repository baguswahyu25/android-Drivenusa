package projectichif.DriveNusa

data class TempUser(
    val username: String = "",
    val email: String = "",
    val password: String = "",       // hanya untuk membuat akun di FirebaseAuth; **TIDAK** disimpan ke Firestore
    val phoneNumber: String = ""
)
