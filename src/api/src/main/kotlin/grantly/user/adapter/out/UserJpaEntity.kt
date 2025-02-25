package grantly.user.adapter.out

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

@Entity
@Table(name = "user")
class UserJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(unique = true, length = 100, nullable = false)
    val email: String,
    @Column(length = 255, nullable = false)
    val password: String,
    @Column(length = 255, nullable = false)
    var name: String,
): BaseEntity() {
    override fun toString() = entityToString(*toStringProperties)

    override fun equals(other: Any?) = entityEquals(other, UserJpaEntity::id, *equalsAndHashCodeProperties)

    override fun hashCode() = entityHashCode(*equalsAndHashCodeProperties)

    companion object {
        // 사용할 프로퍼티 정의
        val toStringProperties = arrayOf(UserJpaEntity::id, UserJpaEntity::email)
        val equalsAndHashCodeProperties = arrayOf(UserJpaEntity::id, UserJpaEntity::email, UserJpaEntity::name)
    }
}
