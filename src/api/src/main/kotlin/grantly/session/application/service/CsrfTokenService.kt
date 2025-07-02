package grantly.session.application.service

import grantly.common.annotations.UseCase
import grantly.common.constants.AuthConstants
import grantly.common.utils.HttpUtil
import grantly.session.application.port.`in`.CsrfTokenUseCase
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRepository
import java.time.Duration

@UseCase
class CsrfTokenService(
    private val csrfTokenRepository: CsrfTokenRepository,
    private val sessionService: SessionService,
) : CsrfTokenUseCase {
    @Value("\${grantly.cookie.domain}")
    private lateinit var cookieDomain: String

    override fun issueCsrfToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): CsrfToken {
        // csrf token 생성
        val csrfToken = csrfTokenRepository.generateToken(request)
        csrfTokenRepository.saveToken(csrfToken, request, response)
        // 세션 토큰 영속화
        sessionService.persistIfAbsent(request)
        // csrf token 쿠키 설정
        setCsrfTokenCookie(response, csrfToken)
        // 세션/device id 쿠키 설정
        sessionService.setCookies(request, response)

        return csrfToken
    }

    fun setCsrfTokenCookie(
        response: HttpServletResponse,
        csrfToken: CsrfToken,
    ) {
        HttpUtil
            .buildCookie(AuthConstants.CSRF_COOKIE_NAME, csrfToken.token)
            .maxAge(Duration.ofSeconds(AuthConstants.CSRF_TOKEN_EXPIRATION).seconds.toInt())
            .domain(cookieDomain)
            .sameSite("Lax")
            .secure(true)
            .httpOnly(false)
            .build(response)
    }
}
