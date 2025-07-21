package grantly.app.adapter.`in`.dto

import grantly.oauth.domain.enums.OAuthClientScope
import grantly.oauth.domain.enums.OAuthGrantType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "OAuth 클라이언트 생성 요청")
data class CreateAppClientRequest(
    @field:NotBlank
    @field:Size(max = 100)
    @field:Schema(description = "클라이언트 제목", example = "My OAuth Client")
    val title: String,
    @field:NotNull
    @field:Schema(description = "리다이렉트 URI 목록", example = "[\"https://example.com/callback\"]")
    val redirectUris: List<String> = emptyList(),
    @field:NotNull
    @field:Schema(description = "스코프 목록", example = "[\"all\"]")
    val scopes: List<OAuthClientScope> = emptyList(),
    @field:NotNull
    @field:Schema(description = "그랜트 타입", example = "authorization_code")
    val grantType: OAuthGrantType,
)
