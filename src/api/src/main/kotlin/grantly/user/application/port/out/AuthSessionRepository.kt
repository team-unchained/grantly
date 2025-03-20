package grantly.user.application.port.out

import grantly.user.domain.AuthSession

interface AuthSessionRepository {
    fun createAuthSession(session: AuthSession): AuthSession
}
