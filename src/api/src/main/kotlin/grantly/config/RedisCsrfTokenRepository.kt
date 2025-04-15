package grantly.config

import grantly.common.constants.AuthConstants
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.security.web.csrf.DefaultCsrfToken
import java.time.Duration
import java.util.UUID

private val log = KotlinLogging.logger {}

class RedisCsrfTokenRepository(
    private val redisTemplate: StringRedisTemplate,
    private val headerName: String = AuthConstants.CSRF_HEADER_NAME,
    private val parameterName: String = "_csrf",
    private val ttl: Duration = Duration.ofSeconds(AuthConstants.CSRF_TOKEN_EXPIRATION),
) : CsrfTokenRepository {
    private fun generateTokenValue(): String = UUID.randomUUID().toString()

    override fun generateToken(request: HttpServletRequest?): CsrfToken {
        val tokenValue = generateTokenValue()
        return DefaultCsrfToken(headerName, parameterName, tokenValue)
    }

    override fun saveToken(
        token: CsrfToken,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val sessionToken = extractSessionToken(request)
        val redisKey = "csrf:${sessionToken.token}"

        log.debug { "Saving CSRF token to Redis: $redisKey" }
        redisTemplate.opsForValue().set(redisKey, token.token, ttl)
    }

    override fun loadToken(request: HttpServletRequest): CsrfToken? {
        val sessionToken = extractSessionToken(request)
        val redisKey = "csrf:${sessionToken.token}"
        val tokenValue = redisTemplate.opsForValue().get(redisKey) ?: return null

        return DefaultCsrfToken(headerName, parameterName, tokenValue)
    }

    private fun extractSessionToken(request: HttpServletRequest) = request.getAttribute(AuthConstants.SESSION_ATTR) as CustomHttpSession
}
