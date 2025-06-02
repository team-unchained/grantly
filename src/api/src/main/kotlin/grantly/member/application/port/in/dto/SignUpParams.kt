package grantly.member.application.port.`in`.dto

data class SignUpParams(
    val email: String,
    val name: String,
    val password: String,
)
