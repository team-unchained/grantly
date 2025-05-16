package grantly.token.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import grantly.token.adapter.out.enums.TokenType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "토큰 도메인 모델")
class Token(
    val id: Long = 0L,
    val token: String,
    val expiresAt: OffsetDateTime,
    val type: TokenType,
    val payload: Map<String, Any> = emptyMap(),
    var isActive: Boolean = true,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
) {
    fun isValid(): Boolean = isActive && expiresAt.isAfter(OffsetDateTime.now())

    inline fun <reified T> getPayloadAs(objectMapper: ObjectMapper): T {
        // Map<String, Any> → JSON → DTO
        val json = objectMapper.writeValueAsString(payload)
        return objectMapper.readValue<T>(json)
    }
}
