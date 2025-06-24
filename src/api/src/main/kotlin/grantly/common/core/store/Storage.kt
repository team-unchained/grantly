package grantly.common.core.store

interface Storage {
    suspend fun put(
        key: String,
        content: ByteArray,
        overwrite: Boolean = false,
    ): String

    suspend fun delete(key: String)

    suspend fun get(key: String): ByteArray

    suspend fun exists(key: String): Boolean
}
