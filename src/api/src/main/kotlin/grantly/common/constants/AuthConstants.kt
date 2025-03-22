package grantly.common.constants

object AuthConstants {
    const val SESSION_COOKIE_NAME = "session_token"
    const val CSRF_COOKIE_NAME = "CSRF-TOKEN"
    const val CSRF_HEADER_NAME = "X-CSRF-Token"
    const val SESSION_TOKEN_EXPIRATION = 60 * 60 * 24L // 24 hours
    const val CSRF_TOKEN_EXPIRATION = 60 * 60L // 1 hour
}
