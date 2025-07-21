package grantly.oauth.adapter.out

import grantly.common.entity.IsMapper
import grantly.oauth.domain.OAuthConsentDomain
import org.springframework.stereotype.Component

@Component
class OAuthConsentMapper : IsMapper<OAuthConsentJpaEntity, OAuthConsentDomain> {
    override fun toDomain(entity: OAuthConsentJpaEntity): OAuthConsentDomain =
        OAuthConsentDomain(
            id = entity.id ?: 0L,
            appClientId = entity.appClientId,
            userId = entity.userId,
            grantedScopes = entity.grantedScopes.toMutableList(),
            consentedAt = entity.consentedAt,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: OAuthConsentDomain): OAuthConsentJpaEntity =
        OAuthConsentJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            userId = domain.userId,
            appClientId = domain.appClientId,
            grantedScopes = domain.grantedScopes.toMutableList(),
            consentedAt = domain.consentedAt,
        )
}
