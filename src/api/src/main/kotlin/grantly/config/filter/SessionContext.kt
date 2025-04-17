package grantly.config.filter

import grantly.user.application.service.SessionService
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
        var sessionToken = sessionService.getSessionTokenCookie(request)
        if (sessionToken == null) {
            val (token, expiresAt) = sessionService.generateSessionToken()
            sessionToken = token
            sessionService.setSessionToken(response, token, expiresAt)
        }
        var deviceId = sessionService.getDeviceIdCookie(request)
        if (deviceId == null) {
            deviceId = sessionService.generateDeviceId()
            sessionService.setDeviceId(response, deviceId)
        }
        sessionService.setHttpSession(request, sessionToken, deviceId)
        log.debug { "Session token: $sessionToken, Device ID: $deviceId" }

        filterChain.doFilter(request, response)
    }
}
