package grantly.common.constants

object AuthConstants {
    const val SESSION_COOKIE_NAME = "session_token"
    const val CSRF_COOKIE_NAME = "csrf_token"
    const val CSRF_HEADER_NAME = "X-CSRF-Token"
    const val TOKEN_EXPIRATION = 60 * 60 * 24L
}
