package id.ias.dekaforyou.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

class BitmapUtil {
    companion object {
        fun getBase64String(inputBitmap: Bitmap, quality: Int): String? {
            val outputStream = ByteArrayOutputStream()
            inputBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val outputBytes = outputStream.toByteArray()
            return Base64.encodeToString(outputBytes, Base64.DEFAULT)
        }
    }
}