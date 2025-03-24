package grantly.user.application.service

import grantly.common.annotations.UseCase
import grantly.user.application.port.`in`.CsrfTokenUseCase
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRepository

@UseCase
class CsrfTokenService(
    private val csrfTokenRepository: CsrfTokenRepository,
) : CsrfTokenUseCase {
    override fun setCsrfToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): CsrfToken {
        val csrfToken = csrfTokenRepository.generateToken(request)
        csrfTokenRepository.saveToken(csrfToken, request, response)

        return csrfToken
    }
}
