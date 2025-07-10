package grantly.oauth.adapter.out.enums

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "OAuth 클라이언트 스코프 열거형")
enum class OAuthClientScope(
    @JsonValue
    val value: String,
    val description: String,
) {
    ALL("all", "모든 권한"),
    ;

    companion object {
        fun fromValue(value: String): OAuthClientScope? = values().find { it.value == value }

        fun isValid(value: String): Boolean = fromValue(value) != null

        fun getAllValues(): List<String> = values().map { it.value }
    }
}
