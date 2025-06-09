package grantly.app.adapter.out

import grantly.common.entity.BaseEntity
import grantly.common.entity.entityEquals
import grantly.common.entity.entityHashCode
import grantly.common.entity.entityToString
import grantly.member.adapter.out.MemberJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "application")
class AppJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(unique = true, length = 50, nullable = false)
    val slug: String,
    @Column(length = 50, nullable = false)
    var name: String,
    @Column(nullable = true)
    var imageUrl: String? = null,
    @Column(nullable = true, length = 512)
    var description: String? = null,
    @Column(name = "owner_id", nullable = false)
    val ownerId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, referencedColumnName = "id", updatable = false, insertable = false)
    val owner: MemberJpaEntity? = null,
    @Column(nullable = false)
    var isActive: Boolean = true,
) : BaseEntity() {
    override fun equals(other: Any?) = entityEquals(other, AppJpaEntity::id)

    override fun hashCode() = entityHashCode(AppJpaEntity::id)

    override fun toString() = entityToString(*toStringProperties)

    companion object {
        val toStringProperties = arrayOf(AppJpaEntity::id, AppJpaEntity::slug, AppJpaEntity::name)
    }
}
