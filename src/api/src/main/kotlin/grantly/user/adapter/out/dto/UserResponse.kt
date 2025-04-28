package grantly.user.adapter.out.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "유저 정보 응답 DTO")
data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
)
