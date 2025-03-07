package grantly.user.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 정보 수정 요청 DTO")
data class UpdateUserRequest(
    val name: String,
)
