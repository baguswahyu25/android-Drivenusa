package projectichif.DriveNusa.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object FileUtils {

    fun getPath(context: Context, uri: Uri?): String? {
        if (uri == null) return null

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null

        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            Log.e("FileUtils", "Error getPath: ${e.message}")
        } finally {
            cursor?.close()
        }
        return null
    }
    fun getMultipartFromUri(
        contentResolver: ContentResolver,
        uri: Uri,
        formKey: String
    ): MultipartBody.Part {

        val inputStream = contentResolver.openInputStream(uri)!!
        val bytes = inputStream.readBytes()
        val requestFile = bytes.toRequestBody("image/*".toMediaType())

        return MultipartBody.Part.createFormData(
            formKey,
            "upload_${System.currentTimeMillis()}.jpg",
            requestFile
        )
    }

}
