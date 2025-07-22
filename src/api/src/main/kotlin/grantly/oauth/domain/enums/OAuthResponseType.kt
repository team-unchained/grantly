package grantly.oauth.domain.enums

enum class OAuthResponseType(val value: String) {
    CODE("code"),  // Authorization Code Grant, PKCE
    TOKEN("token"), // Implicit Grant
}
