package grantly.app.application.port.`in`

import org.springframework.web.multipart.MultipartFile

interface UploadAppImageUseCase {
    fun uploadImage(
        appId: Long,
        ownerId: Long,
        image: MultipartFile,
    ): String
}
