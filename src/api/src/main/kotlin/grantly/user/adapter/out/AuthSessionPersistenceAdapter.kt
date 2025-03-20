package grantly.user.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.user.application.port.out.AuthSessionRepository
import grantly.user.domain.AuthSession

@PersistenceAdapter
class AuthSessionPersistenceAdapter(
    private val authSessionJpaRepository: AuthSessionJpaRepository,
    private val authSessionMapper: AuthSessionMapper,
) : AuthSessionRepository {
    override fun createAuthSession(session: AuthSession): AuthSession {
        session.generateToken()
        val userEntity = authSessionJpaRepository.save(authSessionMapper.toEntity(session))
        return authSessionMapper.toDomain(userEntity)
    }
}
