package grantly.app.adapter.out.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "간소화된 앱 응답 DTO")
data class SimpleAppResponse(
    val id: Long,
    val slug: String,
    val name: String,
    val imageUrl: String? = null,
    val ownerId: Long,
)
