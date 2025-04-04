package grantly.config.filter

import grantly.common.constants.AuthConstants
import grantly.common.exceptions.HttpUnauthorizedException
import grantly.common.utils.HttpUtil
import grantly.config.AuthenticatedUser
import grantly.user.application.port.out.UserRepository
import grantly.user.application.service.SessionService
import grantly.user.domain.AuthSession
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionValidationFilter(
    private val sessionService: SessionService,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        filterChain: jakarta.servlet.FilterChain,
    ) {
        // HttpSession 이 request 에 존재하는지 확인
        val httpSession =
            sessionService.getHttpSession(request) ?: run {
                HttpUtil.writeErrorResponse(response, HttpUnauthorizedException())
                return
            }

        val authSession: AuthSession
        try {
            authSession = sessionService.findSessionByToken(httpSession.token)
        } catch (e: EntityNotFoundException) {
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException())
            return
        }
        // 익명세션이 아닌지 확인
        if (authSession.isAnonymous()) {
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException())
            return
        }
        // 세션이 만료되었는지 확인
        if (!authSession.isValid()) {
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException())
            return
        }

        // Security Context 설정
        val user: User
        try {
            // 유저 정보 조회
            user = userRepository.getUser(authSession.userId!!)
        } catch (e: EntityNotFoundException) {
            sessionService.delete(authSession.id)
            // 세션 쿠키 삭제
            HttpUtil.getCookie(request, AuthConstants.SESSION_COOKIE_NAME)?.let { cookie ->
                HttpUtil.deleteCookie(response, cookie)
            }
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
