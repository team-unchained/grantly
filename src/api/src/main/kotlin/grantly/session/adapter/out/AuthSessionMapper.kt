package grantly.session.adapter.out

import grantly.common.entity.IsMapper
import grantly.session.domain.AuthSessionDomain
import org.springframework.stereotype.Component

@Component
class AuthSessionMapper : IsMapper<AuthSessionJpaEntity, AuthSessionDomain> {
    override fun toDomain(entity: AuthSessionJpaEntity): AuthSessionDomain =
        AuthSessionDomain(
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

    override fun toEntity(domain: AuthSessionDomain): AuthSessionJpaEntity =
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
