package grantly.token.application.service

import grantly.token.adapter.out.enums.TokenType
import grantly.token.application.port.out.TokenRepository
import grantly.token.domain.Token
import io.viascom.nanoid.NanoId
import jakarta.validation.ConstraintViolationException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
) {
    fun createPasswordResetToken(memberId: Long): Token =
        createToken {
            val tokenValue = NanoId.generate()
            Token(
                token = tokenValue,
                expiresAt = OffsetDateTime.now().plusHours(1),
                type = TokenType.PASSWORD_RESET,
                payload = mapOf("memberId" to memberId),
            )
        }

    private fun createToken(tokenFactory: () -> Token): Token {
        var retries = 3
        while (retries > 0) {
            val token = tokenFactory()
            try {
                return tokenRepository.create(token)
            } catch (e: ConstraintViolationException) {
                if (retries == 1) throw e
                retries--
            }
        }
        throw IllegalStateException("Failed to create token after multiple attempts")
    }

    fun findToken(token: String) = tokenRepository.get(token)

    fun deactivateToken(token: Token) {
        tokenRepository.deactivate(token)
    }
}
