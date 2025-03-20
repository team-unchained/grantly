package grantly.user.domain

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.security.SecureRandom
import java.time.OffsetDateTime
import java.util.Base64
import java.util.UUID

@Schema(description = "세션 도메인 모델")
data class AuthSession(
    val id: Long = 0L,
    val userId: Long,
    var token: String? = null,
    val ip: String? = null,
    val userAgent: String? = null,
    var expiresAt: OffsetDateTime? = null,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
) {
    private fun appendExpiresAt() {
        expiresAt = OffsetDateTime.now().plusDays(1)
    }

    fun generateToken(): String {
        val encoder = BCryptPasswordEncoder(12)

        // UUID 생성 (중복 방지)
        val uuid = UUID.randomUUID().toString()
        // 랜덤 바이트 추가
        val randomBytes = ByteArray(16)
        SecureRandom().nextBytes(randomBytes)
        val randomString = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
        // UUID + 랜덤 문자열 조합
        val rawToken = "$uuid-$randomString"
        // 해싱
        val bcryptHash = encoder.encode(rawToken)
        // 255자 제한
        token = bcryptHash.take(255)
        // 만료일시 설정
        appendExpiresAt()

        return token as String
    }

    fun isValid(): Boolean = expiresAt?.isAfter(OffsetDateTime.now()) ?: false
}
