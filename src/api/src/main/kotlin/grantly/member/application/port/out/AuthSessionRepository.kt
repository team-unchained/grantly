package grantly.member.application.port.out

import grantly.member.domain.AuthSession

interface AuthSessionRepository {
    fun updateSession(session: AuthSession): AuthSession

    fun getSessionByMemberId(memberId: Long): AuthSession

    fun getSessionByToken(token: String): AuthSession

    fun createSession(session: AuthSession): AuthSession

    fun deleteSession(id: Long)
}
