package grantly.user.adapter.out

import grantly.common.entity.IsMapper
import grantly.user.domain.User
import org.springframework.stereotype.Component

@Component
class UserMapper : IsMapper<UserJpaEntity, User> {
    override fun toDomain(entity: UserJpaEntity): User =
        User(
            id = entity.id ?: 0L,
            email = entity.email,
            name = entity.name,
            password = entity.password,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: User): UserJpaEntity =
        UserJpaEntity(id = if (domain.id == 0L) null else domain.id, email = domain.email, name = domain.name, password = domain.password)
}
