package grantly.common.core.store

interface FileStorage {
    fun put(
        key: String,
        content: ByteArray,
        overwrite: Boolean = false,
    ): String

    fun delete(key: String)

    fun get(key: String): ByteArray

    fun exists(key: String): Boolean
}
