package grantly.app.domain

import grantly.oauth.adapter.out.enums.OAuthClientScope
import grantly.oauth.adapter.out.enums.OAuthGrantType
import grantly.oauth.application.port.`in`.dto.AuthorizeClientParams
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
) {
    private fun validateRedirectUri(uri: String): Boolean = redirectUris.contains(uri)

    private fun validateScope(requestScopes: List<String>): Boolean {
        if (requestScopes.isEmpty()) return true
        return requestScopes.all { scope ->
            scopes.contains(OAuthClientScope.fromValue(scope))
        }
    }
    
    fun validate(params: AuthorizeClientParams) {
        // 1. 등록된 클라이언트의 grant type이 일치하는지 확인
        if (grantType != OAuthGrantType.fromValue(params.responseType)) {
            throw IllegalArgumentException("Invalid grant type: $grantType") // TODO: 예외 핸들링
        }
        if (!validateRedirectUri(params.redirectUri)) {
            throw IllegalArgumentException("Invalid redirect URI: ${params.redirectUri}") // TODO: 예외 핸들링
        }
        if (!validateScope(params.scopes)) {
            throw IllegalArgumentException("Invalid scope: ${params.scopes}") // TODO: 예외 핸들링
        }
    }
}
