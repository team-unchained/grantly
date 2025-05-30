package grantly.config.filter

import grantly.common.constants.AuthConstants
import grantly.common.exceptions.HttpUnauthorizedException
import grantly.common.utils.HttpUtil
import grantly.config.AuthenticatedMember
import grantly.member.application.port.out.MemberRepository
import grantly.member.application.service.SessionService
import grantly.member.domain.AuthSession
import grantly.member.domain.Member
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class SessionValidationFilter(
    private val sessionService: SessionService,
    private val memberRepository: MemberRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        filterChain: jakarta.servlet.FilterChain,
    ) {
        // HttpSession 이 request 에 존재하는지 확인
        val httpSession =
            sessionService.getHttpSession(request)

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
        val member: Member
        try {
            // 유저 정보 조회
            member = memberRepository.getMember(authSession.memberId!!)
        } catch (e: EntityNotFoundException) {
            sessionService.delete(authSession.id)
            // 세션 쿠키 삭제
            HttpUtil.getCookie(request, AuthConstants.SESSION_COOKIE_NAME)?.let {
                sessionService.unsetCookies(response)
            }
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("User not found"))
            return
        }
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(
                AuthenticatedMember(
                    id = member.id,
                    name = member.name,
                    email = member.email,
                ),
                null,
                emptyList(), // TODO: 추후 member 와 user 의 Role 구분?
            )
        // 다음 필터로 진행
        filterChain.doFilter(request, response)
    }
}
