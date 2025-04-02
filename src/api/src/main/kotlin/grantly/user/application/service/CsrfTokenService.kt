package grantly.user.application.service

import grantly.common.annotations.UseCase
import grantly.common.constants.AuthConstants
import grantly.common.utils.HttpUtil
import grantly.user.application.port.`in`.CsrfTokenUseCase
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRepository
import java.util.UUID

@UseCase
class CsrfTokenService(
    private val csrfTokenRepository: CsrfTokenRepository,
) : CsrfTokenUseCase {
    @Value("\${grantly.cookie.domain}")
    private lateinit var cookieDomain: String

    override fun setCsrfToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): CsrfToken {
        HttpUtil.getCookie(request, AuthConstants.SESSION_COOKIE_NAME)
        val sessionCookie = HttpUtil.getCookie(request, AuthConstants.SESSION_COOKIE_NAME)
        if (sessionCookie == null) {
            // session token 이 없다면 anonymous token 임시 발급
            val anonymousSession = "anonymous-${UUID.randomUUID()}"
            HttpUtil
                .buildCookie(AuthConstants.SESSION_COOKIE_NAME, anonymousSession)
                .maxAge(AuthConstants.CSRF_TOKEN_EXPIRATION.toInt())
                .domain(cookieDomain)
                .sameSite("Lax")
                .secure(true)
                .httpOnly(true)
                .build(response)
            request.setAttribute(AuthConstants.SESSION_COOKIE_NAME, anonymousSession)
        }

        val csrfToken = csrfTokenRepository.generateToken(request)
        csrfTokenRepository.saveToken(csrfToken, request, response)

        return csrfToken
    }
}
