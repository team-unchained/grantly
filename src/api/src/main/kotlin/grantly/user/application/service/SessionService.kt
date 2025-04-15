package grantly.user.application.service

import grantly.common.constants.AuthConstants
import grantly.common.utils.HttpUtil
import grantly.config.CustomHttpSession
import grantly.user.application.port.out.AuthSessionRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 세션과 관련된 서비스.
 */
@Component
class SessionService(
    private val sessionRepository: AuthSessionRepository,
) {
    @Value("\${grantly.cookie.domain}")
    private lateinit var cookieDomain: String

    fun getSessionTokenCookie(request: HttpServletRequest): String? {
        HttpUtil
            .getCookie(request, AuthConstants.SESSION_COOKIE_NAME)
            ?.let { cookie ->
                return cookie.value
            } ?: return null
    }

    fun getDeviceIdCookie(request: HttpServletRequest): String? {
        HttpUtil
            .getCookie(request, AuthConstants.DEVICE_ID_COOKIE_NAME)
            ?.let { cookie ->
                return cookie.value
            } ?: return null
    }

    fun setSessionToken(
        response: HttpServletResponse,
        token: String,
        expiresAt: OffsetDateTime,
    ) {
        HttpUtil
            .buildCookie(AuthConstants.SESSION_COOKIE_NAME, token)
            .maxAge(
                Duration.between(OffsetDateTime.now(), expiresAt).seconds.toInt(),
            ).domain(cookieDomain)
            .sameSite("Lax")
            .secure(true)
            .httpOnly(true)
            .build(response)
    }

    fun setDeviceId(
        response: HttpServletResponse,
        deviceId: String,
    ) {
        HttpUtil
            .buildCookie(AuthConstants.DEVICE_ID_COOKIE_NAME, deviceId)
            .maxAge(Integer.MAX_VALUE)
            .domain(cookieDomain)
            .sameSite("Lax")
            .secure(true)
            .httpOnly(true)
            .build(response)
    }

    fun generateSessionToken(): Pair<String, OffsetDateTime> =
        Pair(UUID.randomUUID().toString(), OffsetDateTime.now().plusSeconds(AuthConstants.SESSION_TOKEN_EXPIRATION))

    fun generateDeviceId(): String = UUID.randomUUID().toString()

    fun setHttpSession(
        request: HttpServletRequest,
        token: String,
        deviceId: String,
    ) {
        val httpSession = CustomHttpSession(token, deviceId)
        request.setAttribute(AuthConstants.SESSION_ATTR, httpSession)
    }
}
