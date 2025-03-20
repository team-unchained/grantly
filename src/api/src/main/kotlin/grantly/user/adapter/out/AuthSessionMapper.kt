package grantly.user.adapter.out

import grantly.common.entity.IsMapper
import grantly.user.domain.AuthSession
import org.springframework.stereotype.Component

@Component
class AuthSessionMapper : IsMapper<AuthSessionJpaEntity, AuthSession> {
    override fun toDomain(entity: AuthSessionJpaEntity): AuthSession =
        AuthSession(
            id = entity.id ?: 0L,
            userId = entity.userId,
            ip = entity.ip,
            token = entity.token,
            userAgent = entity.userAgent,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: AuthSession): AuthSessionJpaEntity {
        // TODO: 다른 exception?
        val token = domain.token ?: throw IllegalArgumentException("Token cannot be null")
        val expiresAt = domain.expiresAt ?: throw IllegalArgumentException("ExpiresAt cannot be null")

        return AuthSessionJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            userId = domain.userId,
            ip = domain.ip,
            token = token,
            expiresAt = expiresAt,
            userAgent = domain.userAgent,
        )
    }
}
