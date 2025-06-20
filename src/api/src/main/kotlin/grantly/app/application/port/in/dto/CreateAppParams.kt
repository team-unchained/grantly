package grantly.app.application.port.`in`.dto

data class CreateAppParams(
    val name: String,
    val description: String? = null,
    val ownerId: Long,
)
