package grantly.user.adapter.`in`.dto

data class SignUpRequest(
    val email: String,
    val name: String,
    val password: String
)