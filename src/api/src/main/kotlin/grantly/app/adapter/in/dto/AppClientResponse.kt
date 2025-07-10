package grantly.app.adapter.`in`.dto

import grantly.app.domain.AppClientDomain
import grantly.oauth.adapter.out.enums.OAuthClientScope
import grantly.oauth.adapter.out.enums.OAuthGrantType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "OAuth 클라이언트 응답")
data class AppClientResponse(
    @field:Schema(description = "앱 ID")
    val appId: Long,
    @field:Schema(description = "클라이언트 제목")
    val title: String,
    @field:Schema(description = "OAuth 클라이언트 ID")
    val clientId: String,
    @field:Schema(description = "OAuth 클라이언트 시크릿")
    val clientSecret: String,
    @field:Schema(description = "리다이렉트 URI 목록")
    val redirectUris: List<String>,
    @field:Schema(description = "스코프 목록")
    val scopes: List<OAuthClientScope>,
    @field:Schema(description = "그랜트 타입")
    val grantType: OAuthGrantType,
) {
    companion object {
        fun from(domain: AppClientDomain): AppClientResponse =
            AppClientResponse(
                appId = domain.appId,
                title = domain.title,
                clientId = domain.clientId,
                clientSecret = domain.clientSecret,
                redirectUris = domain.redirectUris,
                scopes = domain.scopes,
                grantType = domain.grantType,
            )
    }
}
