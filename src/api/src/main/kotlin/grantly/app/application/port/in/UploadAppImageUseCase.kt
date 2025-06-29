package grantly.app.application.port.`in`

import org.springframework.web.multipart.MultipartFile

interface UploadAppImageUseCase {
    fun uploadImage(
        appSlug: String,
        ownerId: Long,
        image: MultipartFile,
    ): String
}
