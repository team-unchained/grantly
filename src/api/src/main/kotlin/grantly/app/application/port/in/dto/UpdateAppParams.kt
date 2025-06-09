package grantly.app.application.port.`in`.dto

data class UpdateAppParams(
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
)
