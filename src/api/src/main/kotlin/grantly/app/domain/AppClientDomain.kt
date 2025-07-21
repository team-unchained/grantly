package grantly.app.domain

import grantly.oauth.domain.enums.OAuthClientScope
import grantly.oauth.domain.enums.OAuthGrantType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "앱 OAuth 클라이언트 도메인 모델")
data class AppClientDomain(
    val id: Long = 0L,
    val appId: Long,
    var title: String,
    var clientId: String,
    var clientSecret: String,
    var redirectUris: MutableList<String> = mutableListOf(),
    var scopes: MutableList<OAuthClientScope> = mutableListOf(),
    var grantType: OAuthGrantType,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
)
