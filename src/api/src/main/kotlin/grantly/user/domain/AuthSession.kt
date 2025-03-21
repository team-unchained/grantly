package grantly.user.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "세션 도메인 모델")
data class AuthSession(
    val id: Long = 0L,
    val userId: Long,
    var token: String,
    var csrfToken: String,
    val ip: String? = null,
    val userAgent: String? = null,
    var expiresAt: OffsetDateTime,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
) {
    fun isValid(): Boolean = expiresAt.isAfter(OffsetDateTime.now())
}
