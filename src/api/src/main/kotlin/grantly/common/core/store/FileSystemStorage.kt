package grantly.common.core.store

import grantly.common.core.store.exceptions.NoSuchResourceException
import grantly.common.core.store.exceptions.StoreOperationFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private val log = KotlinLogging.logger {}

class FileSystemStorage(
    private val storageName: String,
    private val rootDir: String,
) : Storage {
    private fun String.ensureLeadingSlash(): String = if (this.startsWith("/")) this else "/$this"

    private fun resolveStoragePath(): Path = Paths.get(rootDir, storageName)

    private fun resolvePath(key: String): Path = resolveStoragePath().resolve(key)

    override suspend fun put(
        key: String,
        content: ByteArray,
        overwrite: Boolean,
    ): String =
        withContext(Dispatchers.IO) {
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

            key.ensureLeadingSlash()
        }

    override suspend fun get(key: String): ByteArray =
        withContext(Dispatchers.IO) {
            val path = resolvePath(key)
            if (!Files.exists(path)) {
                throw NoSuchResourceException("File not found: $key")
            }
            Files.readAllBytes(path)
        }

    override suspend fun delete(key: String): Unit =
        withContext(Dispatchers.IO) {
            val path = resolvePath(key)
            try {
                Files.deleteIfExists(path)
            } catch (e: IOException) {
                throw StoreOperationFailedException("Failed to delete file: $key", e)
            }
        }

    override suspend fun exists(key: String): Boolean = withContext(Dispatchers.IO) { Files.exists(resolvePath(key)) }
}
