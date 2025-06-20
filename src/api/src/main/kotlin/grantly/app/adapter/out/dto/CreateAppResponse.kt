package grantly.app.adapter.out.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "앱 생성 응답 DTO")
data class CreateAppResponse(
    val app: AppResponse,
)
