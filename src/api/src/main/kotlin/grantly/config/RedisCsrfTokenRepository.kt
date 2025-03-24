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
    private val sessionTokenName: String = AuthConstants.SESSION_COOKIE_NAME,
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
        // session token 이 없다면 anonymous token 임시 발급
        // TODO: anonymous csrf token 의 ttl 은 기본 ttl 보다 적게 설정?
        val sessionToken = extractSessionToken(request) ?: ("anonymous-" + generateTokenValue())
        val redisKey = "csrf:$sessionToken"

        log.debug { "Saving CSRF token to Redis: $redisKey" }
        redisTemplate.opsForValue().set(redisKey, token.token, ttl)
    }

    override fun loadToken(request: HttpServletRequest): CsrfToken? {
        val sessionToken = extractSessionToken(request) ?: return null
        val redisKey = "csrf:$sessionToken"
        val tokenValue = redisTemplate.opsForValue().get(redisKey) ?: return null

        return DefaultCsrfToken(headerName, parameterName, tokenValue)
    }

    private fun extractSessionToken(request: HttpServletRequest): String? =
        request.cookies?.firstOrNull { it.name == sessionTokenName }?.value
}
