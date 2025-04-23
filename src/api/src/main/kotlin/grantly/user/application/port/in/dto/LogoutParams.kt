package grantly.user.application.port.`in`.dto

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

data class LogoutParams(
    val request: HttpServletRequest,
    val response: HttpServletResponse,
)
