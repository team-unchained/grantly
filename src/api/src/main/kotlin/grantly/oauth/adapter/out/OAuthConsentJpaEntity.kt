package grantly.oauth.adapter.out

import grantly.app.adapter.out.AppClientJpaEntity
import grantly.common.entity.BaseEntity
import grantly.common.entity.entityEquals
import grantly.common.entity.entityHashCode
import grantly.common.entity.entityToString
import grantly.member.adapter.out.MemberJpaEntity
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "oauth_consent")
class OAuthConsentJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", updatable = false, insertable = false)
    val user: MemberJpaEntity? = null, // FIXME: member -> user 로 이름 바뀔 예정
    @Column(name = "user_id")
    val userId: Long,
    @ManyToOne
    @JoinColumn(
        name = "app_client_id",
        nullable = false,
        referencedColumnName = "id",
        updatable = false,
        insertable = false,
    )
    val appClient: AppClientJpaEntity? = null,
    @Column(name = "app_client_id")
    val appClientId: Long,
    @ElementCollection
    @CollectionTable(name = "oauth_consent_granted_scope", joinColumns = [JoinColumn(name = "consent_id")])
    @Column(name = "granted_scope")
    var grantedScopes: MutableList<String> = mutableListOf(),
    @Column(nullable = false)
    var consentedAt: OffsetDateTime,
) : BaseEntity() {
    override fun equals(other: Any?) = entityEquals(other, OAuthConsentJpaEntity::id)

    override fun hashCode() = entityHashCode(OAuthConsentJpaEntity::id)

    override fun toString() = entityToString(*toStringProperties)

    companion object {
        val toStringProperties =
            arrayOf(
                OAuthConsentJpaEntity::id,
                OAuthConsentJpaEntity::userId,
                OAuthConsentJpaEntity::appClientId,
                OAuthConsentJpaEntity::consentedAt,
            )
    }
}
