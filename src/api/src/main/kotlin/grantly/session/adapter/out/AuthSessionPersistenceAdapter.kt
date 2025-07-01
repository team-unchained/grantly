package grantly.session.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.session.application.port.out.AuthSessionRepository
import grantly.session.domain.AuthSessionDomain
import jakarta.persistence.EntityNotFoundException

@PersistenceAdapter
class AuthSessionPersistenceAdapter(
    private val authSessionJpaRepository: AuthSessionJpaRepository,
    private val authSessionMapper: AuthSessionMapper,
) : AuthSessionRepository {
    override fun updateSession(session: AuthSessionDomain): AuthSessionDomain {
        val sessionEntity = authSessionJpaRepository.save(authSessionMapper.toEntity(session))
        return authSessionMapper.toDomain(sessionEntity)
    }

    override fun getSessionByMemberId(memberId: Long): AuthSessionDomain {
        val sessionEntity = authSessionJpaRepository.findByMemberId(memberId)
        if (sessionEntity.isEmpty) {
            throw EntityNotFoundException("AuthSession not found")
        }
        return authSessionMapper.toDomain(sessionEntity.get())
    }

    override fun getSessionByToken(token: String): AuthSessionDomain {
        val sessionEntity = authSessionJpaRepository.findByToken(token)
        if (sessionEntity.isEmpty) {
            throw EntityNotFoundException("AuthSession not found")
        }
        return authSessionMapper.toDomain(sessionEntity.get())
    }

    override fun createSession(session: AuthSessionDomain): AuthSessionDomain {
        val newSession = session.copy(id = 0L)
        val sessionEntity = authSessionJpaRepository.save(authSessionMapper.toEntity(newSession))
        return authSessionMapper.toDomain(sessionEntity)
    }

    override fun deleteSession(id: Long) = authSessionJpaRepository.deleteById(id)
}
