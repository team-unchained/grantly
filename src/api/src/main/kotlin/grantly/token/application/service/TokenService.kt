package grantly.token.application.service

import grantly.token.adapter.out.enums.TokenType
import grantly.token.application.port.out.TokenRepository
import grantly.token.domain.Token
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
) {
    fun createToken(type: TokenType, expiresAt: OffsetDateTime, payload: Map<String, Any>?): Token {
        if (expiresAt.isBefore(OffsetDateTime.now())) {
            throw IllegalArgumentException("Expiration date must be in the future")
        }

        val tokenValue = when(type) {
            TokenType.PASSWORD_RESET -> {
                UUID.randomUUID().toString()
            }
            else -> throw IllegalArgumentException("Invalid token type")
        }

        val token = Token(token=tokenValue, expiresAt = expiresAt, type=type, payload = payload ?: emptyMap())
        return tokenRepository.createToken(token) // TODO: handle integrity error (unique token value)
    }
}
