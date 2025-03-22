package grantly.user.application.port.`in`

import grantly.user.application.port.`in`.dto.LoginParams
import grantly.user.domain.AuthSession
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface LoginUseCase {
    fun login(params: LoginParams): AuthSession

    fun setCsrfToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    )
}
