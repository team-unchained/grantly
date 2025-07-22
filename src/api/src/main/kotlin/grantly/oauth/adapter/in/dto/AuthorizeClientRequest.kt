package grantly.oauth.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "클라이언트 권한 부여 요청 DTO")
data class AuthorizeClientRequest(
    val clientId: String,
    val scopes: List<String>,
    val redirectUri: String,
    val state: String? = null,
    val responseType: String,
)
