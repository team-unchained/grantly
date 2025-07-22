package grantly.oauth.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.oauth.application.port.out.OAuthConsentRepository
import grantly.oauth.domain.OAuthConsentDomain
import jakarta.persistence.EntityNotFoundException

@PersistenceAdapter
class OAuthConsentPersistenceAdapter(
    private val oAuthConsentJpaRepository: OAuthConsentJpaRepository,
    private val mapper: OAuthConsentMapper,
) : OAuthConsentRepository {
    override fun createConsent(domain: OAuthConsentDomain): OAuthConsentDomain {
        val entity = mapper.toEntity(domain)
        val savedEntity = oAuthConsentJpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun findByAppClientIdAndUserId(
        appClientId: Long,
        userId: Long
    ): OAuthConsentDomain {
        return oAuthConsentJpaRepository
            .findByAppClientIdAndUserId(appClientId, userId)
            .map(mapper::toDomain)
            .orElseThrow { EntityNotFoundException("OAuth consent not found.") }
    }
}
