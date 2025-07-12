package grantly.session.application.port.out

import grantly.session.domain.AuthSessionDomain

interface AuthSessionRepository {
    fun updateSession(session: AuthSessionDomain): AuthSessionDomain

    fun getSessionByMemberId(memberId: Long): AuthSessionDomain

    fun getSessionByToken(token: String): AuthSessionDomain

    fun createSession(session: AuthSessionDomain): AuthSessionDomain

    fun deleteSession(id: Long)
}
