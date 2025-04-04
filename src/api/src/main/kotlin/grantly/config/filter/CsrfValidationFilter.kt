package grantly.config.filter

import grantly.common.constants.AuthConstants
import grantly.common.exceptions.HttpUnauthorizedException
import grantly.common.utils.HttpUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.web.filter.OncePerRequestFilter

class CsrfValidationFilter(
    private val csrfTokenRepository: CsrfTokenRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // unsafe method 인가
        if (!isUnsafeMethod(request)) {
            filterChain.doFilter(request, response)
            return
        }

        // CSRF 토큰이 존재하는가
        val csrfTokenHeader =
            request.getHeader(AuthConstants.CSRF_HEADER_NAME) ?: run {
                HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("CSRF token header not found"))
                return
            }

        // CSRF 토큰이 쿠키에 존재하고 registry 에서 조회 가능한가
        val savedCsrfToken =
            csrfTokenRepository.loadToken(request)
                ?: run {
                    HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("CSRF token not found"))
                    return
                }

        // CSRF 토큰이 일치하는가
        if (csrfTokenHeader != savedCsrfToken.token) {
            HttpUtil.writeErrorResponse(response, HttpUnauthorizedException("CSRF token mismatch"))
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun isUnsafeMethod(request: HttpServletRequest): Boolean {
        val unsafeMethods = listOf("POST", "PUT", "DELETE", "PATCH")
        return unsafeMethods.contains(request.method)
    }
}
