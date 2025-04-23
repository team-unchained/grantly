package grantly.user.application.service

import grantly.common.constants.AuthConstants
import grantly.common.utils.HttpUtil
import grantly.config.CustomHttpSession
import grantly.user.application.port.out.AuthSessionRepository
import grantly.user.domain.AuthSession
import jakarta.persistence.EntityNotFoundException
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
    private val authSessionRepository: AuthSessionRepository,
) {
    @Value("\${grantly.cookie.domain}")
    private lateinit var cookieDomain: String

    fun getSessionTokenCookie(request: HttpServletRequest): Pair<String?, OffsetDateTime?> {
        HttpUtil
            .getCookie(request, AuthConstants.SESSION_COOKIE_NAME)
            ?.let { cookie ->
                return Pair(cookie.value, OffsetDateTime.now().plusSeconds(cookie.maxAge.toLong()))
            } ?: return Pair(null, null)
    }

    fun getDeviceIdCookie(request: HttpServletRequest): String? {
        HttpUtil
            .getCookie(request, AuthConstants.DEVICE_ID_COOKIE_NAME)
            ?.let { cookie ->
                return cookie.value
            } ?: return null
    }

    fun setCookies(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val httpSession = getHttpSession(request)
        setSessionToken(response, httpSession.token, httpSession.expiresAt)
        setDeviceId(response, httpSession.deviceId)
    }

    private fun setSessionToken(
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

    private fun setDeviceId(
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

    fun unsetCookies(response: HttpServletResponse) {
        unsetSessionToken(response)
        unsetDeviceId(response)
    }

    private fun unsetSessionToken(response: HttpServletResponse) {
        HttpUtil
            .buildCookie(AuthConstants.SESSION_COOKIE_NAME, "")
            .maxAge(0)
            .domain(cookieDomain)
            .sameSite("Lax")
            .secure(true)
            .httpOnly(true)
            .build(response)
    }

    private fun unsetDeviceId(response: HttpServletResponse) {
        HttpUtil
            .buildCookie(AuthConstants.DEVICE_ID_COOKIE_NAME, "")
            .maxAge(0)
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
        httpSession: CustomHttpSession,
    ) {
        request.setAttribute(AuthConstants.SESSION_ATTR, httpSession)
    }

    fun getHttpSession(request: HttpServletRequest): CustomHttpSession =
        request.getAttribute(AuthConstants.SESSION_ATTR) as CustomHttpSession

    fun persistIfAbsent(request: HttpServletRequest): AuthSession {
        val httpSession = getHttpSession(request)
        return try {
            authSessionRepository.getSessionByToken(httpSession.token)
        } catch (e: EntityNotFoundException) {
            persist(request, httpSession)
        }
    }

    private fun persist(
        request: HttpServletRequest,
        httpSession: CustomHttpSession,
    ): AuthSession {
        val ip = request.remoteAddr
        val userAgent = request.getHeader("User-Agent")
        val authSession =
            AuthSession(
                token = httpSession.token,
                deviceId = httpSession.deviceId,
                expiresAt = OffsetDateTime.now().plusSeconds(AuthConstants.SESSION_TOKEN_EXPIRATION),
                ip = ip,
                userAgent = userAgent,
            )
        return authSessionRepository.createSession(authSession)
    }

    fun findSessionByToken(token: String): AuthSession = authSessionRepository.getSessionByToken(token)

    fun findSessionByUserId(userId: Long): AuthSession = authSessionRepository.getSessionByUserId(userId)

    fun delete(id: Long) = authSessionRepository.deleteSession(id)

    fun update(authSession: AuthSession): AuthSession = authSessionRepository.updateSession(authSession)
}
