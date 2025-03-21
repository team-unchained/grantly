package grantly.config

import com.fasterxml.jackson.databind.ObjectMapper
import grantly.common.constants.AuthConstants
import grantly.common.exceptions.HttpErrorType
import grantly.user.application.port.out.AuthSessionRepository
import grantly.user.domain.AuthSession
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

private val SAFE_HTTP_METHODS = setOf("GET", "HEAD", "OPTIONS")
private val SKIP_PATTERNS =
    listOf(
        Regex(".*/login.*"),
        Regex(".*/signup.*"),
        Regex(".*/docs.*"),
        Regex(".*/health.*"),
    )

class CustomAuthFilter(
    private val authSessionRepository: AuthSessionRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // 스킵 패턴 체크
        if (SKIP_PATTERNS.any { it.matches(request.requestURI) }) {
            filterChain.doFilter(request, response)
            return
        }

        val sessionToken = request.cookies?.find { it.name == AuthConstants.SESSION_COOKIE_NAME }?.value
        if (sessionToken.isNullOrBlank()) {
            response.writeErrorJson(HttpErrorType.AUTHENTICATION_FAILED, "Session token not found")
            return
        }
        // 세션 토큰 검증
        var session: AuthSession
        try {
            session = authSessionRepository.getSessionByToken(sessionToken)
            if (!session.isValid()) {
                throw EntityNotFoundException()
            }
        } catch (e: EntityNotFoundException) {
            response.writeErrorJson(HttpErrorType.AUTHENTICATION_FAILED, "Invalid session")
            return
        }

        if (!isSafeMethod(request)) {
            // CSRF 토큰 검증
            val csrfToken = request.getHeader(AuthConstants.CSRF_HEADER_NAME)
            if (csrfToken.isNullOrBlank()) {
                response.writeErrorJson(HttpErrorType.AUTHENTICATION_FAILED, "CSRF token not found")
                return
            }
            if (!csrfToken.equals(session.csrfToken)) {
                response.writeErrorJson(HttpErrorType.AUTHENTICATION_FAILED, "CSRF token mismatch")
                return
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun isSafeMethod(request: HttpServletRequest): Boolean = SAFE_HTTP_METHODS.contains(request.method)
}

/**
 * writeErrorJson
 *
 * GlobalExceptionHandler 에서 반환하는 표준화된 에러 응답인 HttpExceptionResponse 와 그 형태를 맞추기 위한 메서드
 */
fun HttpServletResponse.writeErrorJson(
    errorType: HttpErrorType,
    message: String,
    detail: Map<String, Any>? = null,
) {
    this.status = errorType.httpStatus.value()
    this.contentType = "application/json"
    this.characterEncoding = "UTF-8"

    val errorResponse =
        mapOf(
            "code" to this.status,
            "message" to message,
            "detail" to detail,
        )

    val mapper = ObjectMapper().findAndRegisterModules()
    this.writer.write(mapper.writeValueAsString(errorResponse))
}
