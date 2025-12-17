package projectichif.DriveNusa.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun saveProfilePhotoLocal(context: Context, uri: Uri?): String? {
    if (uri == null) return null

    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "profile_photo.jpg")

        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
