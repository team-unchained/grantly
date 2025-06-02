package grantly.member.adapter.out.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "멤버 정보 응답 DTO")
data class MemberResponse(
    val id: Long,
    val email: String,
    val name: String,
)
