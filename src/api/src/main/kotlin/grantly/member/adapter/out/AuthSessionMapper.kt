package grantly.member.adapter.out

import grantly.common.entity.IsMapper
import grantly.member.domain.AuthSession
import org.springframework.stereotype.Component

@Component
class AuthSessionMapper : IsMapper<AuthSessionJpaEntity, AuthSession> {
    override fun toDomain(entity: AuthSessionJpaEntity): AuthSession =
        AuthSession(
            id = entity.id ?: 0L,
            memberId = entity.memberId,
            deviceId = entity.deviceId,
            ip = entity.ip,
            token = entity.token,
            userAgent = entity.userAgent,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: AuthSession): AuthSessionJpaEntity =
        AuthSessionJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            memberId = domain.memberId,
            token = domain.token,
            deviceId = domain.deviceId,
            ip = domain.ip,
            userAgent = domain.userAgent,
            expiresAt = domain.expiresAt,
        )
}
