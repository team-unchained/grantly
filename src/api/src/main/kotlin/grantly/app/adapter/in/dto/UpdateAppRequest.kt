package grantly.app.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "앱 업데이트 요청 DTO")
data class UpdateAppRequest(
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
)
