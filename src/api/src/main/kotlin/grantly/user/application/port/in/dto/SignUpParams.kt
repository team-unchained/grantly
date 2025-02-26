package grantly.user.application.port.`in`.dto

data class SignUpParams(
    val email: String,
    val name: String,
    val password: String,
)
