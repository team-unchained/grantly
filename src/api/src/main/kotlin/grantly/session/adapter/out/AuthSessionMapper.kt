package grantly.session.adapter.out

import aws.smithy.kotlin.runtime.util.type
import grantly.common.entity.IsMapper
import grantly.session.domain.AuthSessionDomain
import grantly.session.domain.SubjectType
import org.springframework.stereotype.Component

@Component
class AuthSessionMapper : IsMapper<AuthSessionJpaEntity, AuthSessionDomain> {
    override fun toDomain(entity: AuthSessionJpaEntity): AuthSessionDomain =
        AuthSessionDomain(
            id = entity.id ?: 0L,
            subjectId = entity.subjectId,
            subjectType = entity.subjectType?.let { SubjectType.from(it) },
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
            subjectId = domain.subjectId,
            subjectType = domain.subjectType?.type,
            token = domain.token,
            deviceId = domain.deviceId,
            ip = domain.ip,
            userAgent = domain.userAgent,
            expiresAt = domain.expiresAt,
        )
}
