package grantly.oauth.domain

import grantly.oauth.adapter.out.enums.OAuthClientScope
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "OAuth 권한 부여 동의 도메인 모델")
data class OAuthConsentDomain(
    val id: Long = 0L,
    val appClientId: Long,
    val userId: Long,
    val grantedScopes: MutableList<OAuthClientScope> = mutableListOf(),
    val consentedAt: OffsetDateTime,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
)
