package grantly.common.core.store

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.mock.web.MockMultipartFile
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileTypeInspectorTest {
    private lateinit var testPngFile: File

    @BeforeAll
    fun setUp() {
        // PNG signature + IHDR chunk (13 bytes minimal structure)
        val pngBytes =
            byteArrayOf(
                0x89.toByte(),
                0x50,
                0x4E,
                0x47,
                0x0D,
                0x0A,
                0x1A,
                0x0A, // PNG signature
                // IHDR chunk (length + type + data + CRC) - dummy minimal
                0x00,
                0x00,
                0x00,
                0x0D, // length = 13
                0x49,
                0x48,
                0x44,
                0x52, // "IHDR"
                0x00,
                0x00,
                0x00,
                0x01, // width: 1
                0x00,
                0x00,
                0x00,
                0x01, // height: 1
                0x08, // bit depth
                0x02, // color type (Truecolor)
                0x00, // compression method
                0x00, // filter method
                0x00, // interlace method
                0x90.toByte(),
                0x77,
                0x53,
                0xDE.toByte(), // dummy CRC
            )

        testPngFile =
            kotlin.io.path
                .createTempFile("test_image", ".png")
                .toFile()
        testPngFile.writeBytes(pngBytes)
        testPngFile.deleteOnExit()
    }

    @Test
    fun `detect mime type from input stream`() {
        val mockMultipartFile =
            MockMultipartFile(
                "image", // 요청 파라미터 이름
                testPngFile.name, // 파일 이름
                null, // MIME 타입
                testPngFile.readBytes(), // 바이트 배열로 된 파일 내용
            )
        val mimeType = FileTypeInspector.detectMimeType(mockMultipartFile.inputStream)
        val ext = FileTypeInspector.detectExtension(mockMultipartFile.inputStream)

        assert(mimeType == "image/png")
        assert(ext == "png")
    }

    @Test
    fun `detect mime type from file`() {
        val mimeType = FileTypeInspector.detectMimeType(testPngFile)
        val ext = FileTypeInspector.detectExtension(testPngFile)

        assert(mimeType == "image/png")
        assert(ext == "png")
    }
}
