package grantly.member.adapter.out

import grantly.common.entity.BaseEntity
import grantly.common.entity.entityEquals
import grantly.common.entity.entityHashCode
import grantly.common.entity.entityToString
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "member")
class MemberJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(unique = true, length = 100, nullable = false)
    val email: String,
    @Column(length = 255, nullable = true)
    val password: String? = null,
    @Column(length = 255, nullable = false)
    var name: String,
    @Column(nullable = true)
    var lastLoginAt: OffsetDateTime? = null,
) : BaseEntity() {
    override fun toString() = entityToString(*toStringProperties)

    override fun equals(other: Any?) = entityEquals(other, MemberJpaEntity::id)

    override fun hashCode() = entityHashCode(MemberJpaEntity::id)

    companion object {
        // 사용할 프로퍼티 정의
        val toStringProperties = arrayOf(MemberJpaEntity::id, MemberJpaEntity::email)
    }
}
