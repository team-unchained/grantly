package grantly.config

import grantly.common.constants.AuthConstants
import grantly.common.exceptions.HttpUnauthorizedException
import grantly.common.utils.HttpUtil
import grantly.user.application.port.out.AuthSessionRepository
import grantly.user.application.port.out.UserRepository
import grantly.user.domain.AuthSession
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

private val log = KotlinLogging.logger {}

class CustomSessionFilter(
    private val authSessionRepository: AuthSessionRepository,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        log.debug { "CustomSessionFilter doFilterInternal" }
        // 세션 토큰이 쿠키에 존재하는가
        val sessionCookie =
            HttpUtil.getCookie(request, AuthConstants.SESSION_COOKIE_NAME)
                ?: run {
                    HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("Session token not found"))
                    return
                }

        // 세션 토큰이 DB 에 존재하는가
        val session: AuthSession
        try {
            session = authSessionRepository.getSessionByToken(sessionCookie.value)
        } catch (e: EntityNotFoundException) {
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("Session token not found"))
            return
        }

        // 세션 토큰이 만료되었는가
        if (!session.isValid()) {
            authSessionRepository.deleteSession(session)
            HttpUtil.deleteCookie(response, sessionCookie)
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("Session token expired"))
            return
        }

        // device id 가 일치하는가
        val deviceIdCookie =
            HttpUtil.getCookie(request, AuthConstants.DEVICE_ID_COOKIE_NAME) ?: run {
                HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("Login required"))
                return
            }
        if (session.deviceId != deviceIdCookie.value) {
            authSessionRepository.deleteSession(session)
            HttpUtil.deleteCookie(response, sessionCookie)
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("Login required"))
            return
        }
        // Security Context 설정
        val user: User
        try {
            // 유저 정보 조회
            user = userRepository.getUser(session.userId)
        } catch (e: EntityNotFoundException) {
            authSessionRepository.deleteSession(session)
            HttpUtil.deleteCookie(response, sessionCookie)
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("User not found"))
            return
        }
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(
                AuthenticatedUser(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                ),
                null,
                emptyList(), // TODO: 추후 member 와 user 의 Role 구분?
            )
        // 다음 필터로 진행
        filterChain.doFilter(request, response)
    }
}
