package grantly.app.application.port.`in`.dto

import grantly.oauth.adapter.out.enums.OAuthClientScope
import grantly.oauth.adapter.out.enums.OAuthGrantType

data class CreateAppClientParams(
    val appId: Long,
    val title: String,
    val redirectUris: List<String> = emptyList(),
    val scopes: List<OAuthClientScope> = emptyList(),
    val grantType: OAuthGrantType,
)
