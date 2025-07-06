package grantly.app.application.port.`in`.dto

import grantly.oauth.adapter.out.enums.OAuthClientScope
import grantly.oauth.adapter.out.enums.OAuthGrantType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateAppClientParams(
    @field:NotNull
    val appId: Long,
    @field:NotBlank
    @field:Size(max = 100)
    val title: String,
    @field:NotNull
    val redirectUris: List<String> = emptyList(),
    @field:NotNull
    val scopes: List<OAuthClientScope> = emptyList(),
    @field:NotNull
    val grantType: OAuthGrantType,
)
