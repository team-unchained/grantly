package grantly.token.domain

import grantly.token.adapter.out.enums.TokenType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "토큰 도메인 모델")
class Token(
    val id: Long = 0L,
    val token: String,
    val expiresAt: OffsetDateTime,
    val type: TokenType,
    val payload: Map<String, Any>? = null,
    var isActive: Boolean = true,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
)
