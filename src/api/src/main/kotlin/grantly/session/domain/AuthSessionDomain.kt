package grantly.session.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "세션 도메인 모델")
data class AuthSessionDomain(
    val id: Long = 0L,
    var subjectId: Long? = null,
    var subjectType: SubjectType? = null,
    var token: String,
    var deviceId: String,
    var ip: String? = null,
    var userAgent: String? = null,
    var expiresAt: OffsetDateTime,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
) {
    fun isValid(): Boolean = expiresAt.isAfter(OffsetDateTime.now())

    fun isAnonymous(): Boolean = subjectId == null

    fun updateMeta(
        ip: String?,
        userAgent: String?,
        deviceId: String?,
    ) {
        this.ip = ip ?: this.ip
        this.userAgent = userAgent ?: this.userAgent
        this.deviceId = deviceId ?: this.deviceId
    }

    fun connectMember(memberId: Long) {
        this.subjectType = SubjectType.MEMBER
        this.subjectId = memberId
    }

    fun replaceToken(
        value: String,
        expiresAt: OffsetDateTime,
    ) {
        token = value
        this.expiresAt = expiresAt
    }
}
