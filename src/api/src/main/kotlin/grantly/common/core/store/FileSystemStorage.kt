package grantly.common.core.store

import grantly.common.core.store.exceptions.NoSuchResourceException
import grantly.common.core.store.exceptions.StoreOperationFailedException
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileSystemStorage(
    private val rootDir: String,
) : FileStorage {
    private fun resolvePath(key: String): Path {
        val sanitizedKey = key.replace("..", "").replace(File.separatorChar, '_')
        return Paths.get(rootDir, sanitizedKey)
    }

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
            throw StoreOperationFailedException("Failed to store file: $key", e)
        }
        return path.toString()
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

    override fun exists(key: String): Boolean = Files.exists(Paths.get("$rootDir/$key"))
}
