package grantly.app.application.port.`in`

interface DeleteAppImageUseCase {
    fun deleteImage(
        appSlug: String,
        ownerId: Long,
    )
}
