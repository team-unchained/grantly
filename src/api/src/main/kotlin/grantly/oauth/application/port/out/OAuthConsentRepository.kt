package grantly.oauth.application.port.out

import grantly.oauth.domain.OAuthConsentDomain

interface OAuthConsentRepository {
    fun createConsent(domain: OAuthConsentDomain): OAuthConsentDomain
}
