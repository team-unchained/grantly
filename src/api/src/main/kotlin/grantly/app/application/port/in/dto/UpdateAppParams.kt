package grantly.app.application.port.`in`.dto

data class UpdateAppParams(
    val slug: String,
    val name: String,
    val description: String? = null,
    val ownerId: Long,
)
