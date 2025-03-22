package grantly.user.application.port.`in`

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface CsrfTokenUseCase {
    fun setCsrfToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    )
}
