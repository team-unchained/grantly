package grantly.token.adapter.out

import grantly.common.entity.IsMapper
import grantly.token.adapter.out.enums.TokenType
import grantly.token.domain.TokenDomain
import org.springframework.stereotype.Component

@Component
class TokenMapper : IsMapper<TokenJpaEntity, TokenDomain> {
    override fun toDomain(entity: TokenJpaEntity): TokenDomain =
        TokenDomain(
            id = entity.id ?: 0L,
            token = entity.token,
            isActive = entity.isActive,
            type = TokenType.from(entity.type),
            payload = entity.payload,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: TokenDomain): TokenJpaEntity =
        TokenJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            token = domain.token,
            isActive = domain.isActive,
            type = domain.type.type,
            payload = domain.payload,
            expiresAt = domain.expiresAt,
        )
}
