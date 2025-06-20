package grantly.app.application.port.`in`

interface DeleteAppImageUseCase {
    fun deleteImage(
        appId: Long,
        ownerId: Long,
    )
}
