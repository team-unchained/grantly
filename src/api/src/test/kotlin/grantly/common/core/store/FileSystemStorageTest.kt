package grantly.common.core.store

import grantly.common.core.store.exceptions.NoSuchResourceException
import grantly.common.core.store.exceptions.StoreOperationFailedException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileSystemStorageTest {
    private val storageName: String = "storage"
    private val rootDir: String = "test"
    private val fileSystemStorage = FileSystemStorage(storageName, rootDir)

    @AfterEach
    fun cleanup() {
        // Clean up the test storage directory after each test
        val dir = File(rootDir)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }

    @Test
    @DisplayName("기본 파일 저장")
    fun put() {
        // given
        val key = "test.txt"
        val content = "Hello, World!".toByteArray()
        // when
        fileSystemStorage.put(key, content)
        // then
        assertDoesNotThrow { readFile("${getStoragePath()}/$key") }
    }

    @Test
    @DisplayName("파일 덮어쓰면서 저장")
    fun `overwrite file on put`() {
        // given
        writeFile("${getStoragePath()}/test.txt", "Initial content")
        // when
        fileSystemStorage.put("test.txt", "Overwritten".toByteArray(), true)
        // then
        assert(readFile("${getStoragePath()}/test.txt") == "Overwritten")
    }

    @Test
    @DisplayName("overwrite가 비활성화된 상태에서 파일 덮어쓰기를 시도할 때 예외 발생")
    fun `throw exception when overwrite is disabled`() {
        // given
        writeFile("${getStoragePath()}/test.txt", "Initial content")
        // when & then
        assertThrows<StoreOperationFailedException> {
            fileSystemStorage.put(
                "test.txt",
                "Overwritten".toByteArray(),
                false,
            )
        }
    }

    @Test
    @DisplayName("기본 파일 읽기")
    fun get() {
        // given
        val key = "test.txt"
        val content = "Hello, World!"
        writeFile("${getStoragePath()}/$key", content)
        // when
        val result = fileSystemStorage.get(key)
        // then
        assert(result.contentEquals(content.toByteArray())) { "Content does not match expected value." }
    }

    @Test
    @DisplayName("존재하지 않는 파일에 대한 읽기 시도는 예외를 발생시킴")
    fun `throw exception on get`() {
        // given
        val key = "test.txt"
        // when & then
        assertThrows<NoSuchResourceException> {
            fileSystemStorage.get(key)
        }
    }

    @Test
    @DisplayName("파일 삭제")
    fun delete() {
        // given
        val key = "test.txt"
        val content = "Hello, World!"
        writeFile("${getStoragePath()}/$key", content)
        // when
        fileSystemStorage.delete(key)
        // then
        assertThrows<IllegalArgumentException> { readFile(key) }
    }

    @Test
    @DisplayName("파일 존재 여부 확인")
    fun exists() {
        // given
        val key = "test.txt"
        val content = "Hello, World!"
        writeFile("${getStoragePath()}/$key", content)
        // when
        val exists = fileSystemStorage.exists(key)
        // then
        assert(exists) { "File should exist but was not found." }
    }

    private fun readFile(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }
        return file.readText(Charsets.UTF_8)
    }

    private fun writeFile(
        filePath: String,
        content: String,
    ) {
        val file = File(filePath)
        file.parentFile.mkdirs() // Ensure parent directories exist
        file.writeText(content, Charsets.UTF_8)
    }

    private fun getStoragePath() = "$rootDir/$storageName"
}
