package grantly.app.application.port.`in`.dto

import grantly.oauth.adapter.out.enums.OAuthClientScope
import grantly.oauth.adapter.out.enums.OAuthGrantType
import jakarta.validation.constraints.NotBlank

data class UpdateAppClientParams(
    val appId: Long,
    @field:NotBlank
    val clientId: String,
    val title: String,
    val redirectUris: List<String> = emptyList(),
    val scopes: List<OAuthClientScope> = emptyList(),
    val grantType: OAuthGrantType,
)
