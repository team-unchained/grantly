package grantly.user.application.port.`in`.dto

data class LoginParams(
    val email: String,
    val password: String,
    val deviceId: String?,
    val ip: String?,
    val userAgent: String?,
)
