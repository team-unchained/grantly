package grantly.app.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "앱 생성 요청 DTO")
data class CreateAppRequest(
    val name: String,
    val description: String? = null,
)
