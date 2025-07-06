package grantly.oauth.adapter.out.enums

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "OAuth 권한 부여 타입 열거형")
enum class OAuthGrantType(
    @JsonValue
    val value: String,
    val description: String,
) {
    AUTHORIZATION_CODE("authorization_code", "인증 코드"),
    IMPLICIT("implicit", "암묵적 권한 부여"),
    CLIENT_CREDENTIALS("client_credentials", "클라이언트 자격 증명"),
    REFRESH_TOKEN("refresh_token", "리프레시 토큰"),
    ;

    companion object {
        fun fromValue(value: String): OAuthGrantType? = values().find { it.value == value }

        fun isValid(value: String): Boolean = fromValue(value) != null

        fun getAllValues(): List<String> = values().map { it.value }
    }
}
