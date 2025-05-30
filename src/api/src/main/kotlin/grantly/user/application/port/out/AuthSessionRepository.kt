package grantly.user.application.port.out

import grantly.user.domain.AuthSession

interface AuthSessionRepository {
    fun updateSession(session: AuthSession): AuthSession

    fun getSessionByUserId(userId: Long): AuthSession

    fun getSessionByToken(token: String): AuthSession

    fun createSession(session: AuthSession): AuthSession

    fun deleteSession(id: Long)
}
