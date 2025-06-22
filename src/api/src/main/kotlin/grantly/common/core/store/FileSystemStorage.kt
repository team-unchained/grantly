package grantly.common.core.store

import grantly.common.core.store.exceptions.NoSuchResourceException
import grantly.common.core.store.exceptions.StoreOperationFailedException
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private val log = KotlinLogging.logger {}

class FileSystemStorage(
    private val storageName: String,
    private val rootDir: String,
) : FileStorage {
    private fun String.ensureLeadingSlash(): String = if (this.startsWith("/")) this else "/$this"

    private fun resolveStoragePath(): Path = Paths.get(rootDir, storageName)

    private fun resolvePath(key: String): Path = resolveStoragePath().resolve(key)

    override fun put(
        key: String,
        content: ByteArray,
        overwrite: Boolean,
    ): String {
        if (!overwrite && exists(key)) {
            throw StoreOperationFailedException("File already exists: $key")
        }

        val path = resolvePath(key)
        try {
            Files.createDirectories(path.parent)
            Files.write(path, content)
        } catch (e: IOException) {
            log.error { e.message }
            throw StoreOperationFailedException("Failed to store file: $key", e)
        }
        return key.ensureLeadingSlash()
    }

    override fun get(key: String): ByteArray {
        val path = resolvePath(key)
        if (!Files.exists(path)) {
            throw NoSuchResourceException("File not found: $key")
        }
        return Files.readAllBytes(path)
    }

    override fun delete(key: String) {
        val path = resolvePath(key)
        try {
            Files.deleteIfExists(path)
        } catch (e: IOException) {
            throw StoreOperationFailedException("Failed to delete file: $key", e)
        }
    }

    override fun exists(key: String): Boolean = Files.exists(resolvePath(key))
}
