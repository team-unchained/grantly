package grantly.token.adapter.out.enums

/**
 * 토큰의 유형을 나타내는 Enum 클래스
 *
 * @property type 토큰 유형을 나타내는 정수값 (0: 비밀번호 재설정)
 */
enum class TokenType(
    val type: Int,
) {
    PASSWORD_RESET(0),
    ;

    companion object {
        fun from(type: Int): TokenType =
            entries.firstOrNull { it.type == type }
                ?: throw IllegalArgumentException("Invalid token type: $type")
    }
}
