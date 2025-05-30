package grantly.member.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "멤버 정보 수정 요청 DTO")
data class UpdateMemberRequest(
    val name: String,
)
