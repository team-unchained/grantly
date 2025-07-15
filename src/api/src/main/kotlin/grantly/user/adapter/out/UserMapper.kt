package grantly.user.adapter.out

import grantly.common.entity.IsMapper
import grantly.user.domain.UserDomain
import org.springframework.stereotype.Component

@Component
class UserMapper : IsMapper<UserJpaEntity, UserDomain> {
    override fun toDomain(entity: UserJpaEntity): UserDomain =
        UserDomain(
            id = entity.id ?: 0L,
            email = entity.email,
            name = entity.name,
            password = entity.password,
            lastLoginAt = entity.lastLoginAt,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: UserDomain): UserJpaEntity =
        UserJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            email = domain.email,
            name = domain.name,
            password = domain.password,
            lastLoginAt = domain.lastLoginAt,
        )
}
