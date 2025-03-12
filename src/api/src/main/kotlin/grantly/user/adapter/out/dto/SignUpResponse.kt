package grantly.user.adapter.out.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 성공 응답 DTO")
data class SignUpResponse(
    val user: UserResponse,
)
