package grantly.user.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.user.application.port.out.AuthSessionRepository
import grantly.user.domain.AuthSession
import jakarta.persistence.EntityNotFoundException

@PersistenceAdapter
class AuthSessionPersistenceAdapter(
    private val authSessionJpaRepository: AuthSessionJpaRepository,
    private val authSessionMapper: AuthSessionMapper,
) : AuthSessionRepository {
    override fun createAuthSession(session: AuthSession): AuthSession {
        val userEntity = authSessionJpaRepository.save(authSessionMapper.toEntity(session))
        return authSessionMapper.toDomain(userEntity)
    }

    override fun getSessionByUserId(userId: Long): AuthSession {
        val sessionEntity = authSessionJpaRepository.findByUserId(userId)
        if (sessionEntity.isEmpty) {
            throw EntityNotFoundException("AuthSession not found")
        }
        return authSessionMapper.toDomain(sessionEntity.get())
    }

    override fun getSessionByToken(token: String): AuthSession {
        val sessionEntity = authSessionJpaRepository.findByToken(token)
        if (sessionEntity.isEmpty) {
            throw EntityNotFoundException("AuthSession not found")
        }
        return authSessionMapper.toDomain(sessionEntity.get())
    }
}
