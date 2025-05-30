package grantly.member.adapter.out

import grantly.common.entity.IsMapper
import grantly.member.domain.Member
import org.springframework.stereotype.Component

@Component
class MemberMapper : IsMapper<MemberJpaEntity, Member> {
    override fun toDomain(entity: MemberJpaEntity): Member =
        Member(
            id = entity.id ?: 0L,
            email = entity.email,
            name = entity.name,
            password = entity.password,
            lastLoginAt = entity.lastLoginAt,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: Member): MemberJpaEntity =
        MemberJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            email = domain.email,
            name = domain.name,
            password = domain.password,
            lastLoginAt = domain.lastLoginAt,
        )
}
