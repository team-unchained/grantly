package grantly.config.filter

import grantly.config.CustomHttpSession
import grantly.member.application.service.SessionService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

val log = KotlinLogging.logger {}

@Component
class SessionContext(
    private val sessionService: SessionService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        var (sessionToken, expiresAt) = sessionService.getSessionTokenCookie(request)
        if (sessionToken == null) {
            val (newSessionToken, newExpiresAt) = sessionService.generateSessionToken()
            sessionToken = newSessionToken
            expiresAt = newExpiresAt
        }
        var deviceId = sessionService.getDeviceIdCookie(request)
        if (deviceId == null) {
            deviceId = sessionService.generateDeviceId()
        }
        val httpSession = CustomHttpSession(sessionToken, expiresAt!!, deviceId)
        sessionService.setHttpSession(request, httpSession)
        log.debug { "Session token: $sessionToken, Device ID: $deviceId" }

        filterChain.doFilter(request, response)
    }
}
