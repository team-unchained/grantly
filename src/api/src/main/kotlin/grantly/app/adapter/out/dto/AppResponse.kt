package grantly.app.adapter.out.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "앱 응답 DTO")
data class AppResponse(
    val id: Long,
    val slug: String,
    val name: String,
    val imageUrl: String? = null,
    val description: String? = null,
    val ownerId: Long,
    val createdAt: OffsetDateTime,
    val modifiedAt: OffsetDateTime? = null,
)
