package grantly.app.application.port.`in`.dto

data class UpdateAppParams(
    val id: Long,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val ownerId: Long,
)
