package grantly.user.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 요청 DTO")
data class SignUpRequest(
    val email: String,
    val name: String,
    val password: String,
)
