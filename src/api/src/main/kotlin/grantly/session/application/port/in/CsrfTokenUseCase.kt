package grantly.session.application.port.`in`

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfToken

interface CsrfTokenUseCase {
    fun issueCsrfToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): CsrfToken
}
