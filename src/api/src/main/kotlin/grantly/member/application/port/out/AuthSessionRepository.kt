package grantly.member.application.port.out

import grantly.member.domain.AuthSessionDomain

interface AuthSessionRepository {
    fun updateSession(session: AuthSessionDomain): AuthSessionDomain

    fun getSessionByMemberId(memberId: Long): AuthSessionDomain

    fun getSessionByToken(token: String): AuthSessionDomain

    fun createSession(session: AuthSessionDomain): AuthSessionDomain

    fun deleteSession(id: Long)
}
