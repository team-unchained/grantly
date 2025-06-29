package grantly.common.core.store

import org.apache.tika.Tika
import org.apache.tika.mime.MimeTypes
import java.io.File
import java.io.InputStream

object FileTypeInspector {
    private val tika = Tika()

    fun detectMimeType(inputStream: InputStream): String = tika.detect(inputStream)

    fun detectMimeType(file: File): String = tika.detect(file)

    fun detectExtension(inputStream: InputStream): String? {
        val mimeType = detectMimeType(inputStream)
        return getExtensionFromMime(mimeType)
    }

    fun detectExtension(file: File): String? {
        val mimeType = detectMimeType(file)
        return getExtensionFromMime(mimeType)
    }

    private fun getExtensionFromMime(mimeType: String): String? =
        try {
            val mimeTypes = MimeTypes.getDefaultMimeTypes()
            mimeTypes.forName(mimeType).extension.removePrefix(".")
        } catch (e: Exception) {
            null
        }
}
