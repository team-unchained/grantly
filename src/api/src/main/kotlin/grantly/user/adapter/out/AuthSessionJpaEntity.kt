package grantly.user.adapter.out

import grantly.common.entity.BaseEntity
import grantly.common.entity.entityEquals
import grantly.common.entity.entityHashCode
import grantly.common.entity.entityToString
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(
    name = "auth_session",
)
class AuthSessionJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true, length = 50)
    val token: String,
    @Column(nullable = false, length = 50)
    val deviceId: String,
    @Column(nullable = true, length = 255)
    val userAgent: String? = null,
    @Column(nullable = true, length = 45)
    val ip: String? = null,
    @Column(nullable = false)
    val expiresAt: OffsetDateTime,
    @Column(name = "user_id", nullable = true)
    val userId: Long? = null,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, referencedColumnName = "id", updatable = false, insertable = false)
    val user: UserJpaEntity? = null,
) : BaseEntity() {
    override fun toString() = entityToString(*toStringProperties)

    override fun equals(other: Any?) = entityEquals(other, AuthSessionJpaEntity::id)

    override fun hashCode() = entityHashCode(AuthSessionJpaEntity::id)

    companion object {
        val toStringProperties =
            arrayOf(AuthSessionJpaEntity::id, AuthSessionJpaEntity::userId, AuthSessionJpaEntity::token)
    }
}
