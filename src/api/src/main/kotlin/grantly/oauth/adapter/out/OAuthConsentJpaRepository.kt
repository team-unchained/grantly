package grantly.oauth.adapter.out

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OAuthConsentJpaRepository : JpaRepository<OAuthConsentJpaEntity, Long> {
    fun findByAppClientIdAndUserId(
        appClientId: Long,
        userId: Long,
    ): Optional<OAuthConsentJpaEntity>
}
