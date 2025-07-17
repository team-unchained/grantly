package grantly.oauth.application.port.`in`.dto

data class AuthorizeClientParams(
    val clientId: String,
    val scopes: List<String>,
    val redirectUri: String,
    val state: String? = null,
    val responseType: String,
    val userId: Long,
)
