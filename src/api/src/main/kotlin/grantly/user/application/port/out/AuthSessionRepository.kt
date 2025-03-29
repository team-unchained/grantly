package grantly.user.application.port.out

import grantly.user.domain.AuthSession

interface AuthSessionRepository {
    fun upsertAuthSession(session: AuthSession): AuthSession

    fun getSessionByUserId(userId: Long): AuthSession

    fun getSessionByToken(token: String): AuthSession
}
