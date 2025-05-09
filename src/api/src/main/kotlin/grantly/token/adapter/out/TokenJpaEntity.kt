package grantly.token.adapter.out

import grantly.common.entity.BaseEntity
import grantly.common.entity.entityEquals
import grantly.common.entity.entityHashCode
import grantly.common.entity.entityToString
import grantly.token.adapter.out.enums.TokenTypeConverter
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.OffsetDateTime

@Entity
@Table(name = "token")
class TokenJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(unique = true, length = 50, nullable = false)
    val token: String,
    @Column(nullable = false)
    val expiresAt: OffsetDateTime,
    @Convert(converter = TokenTypeConverter::class)
    @Column(nullable = false)
    val type: Int,
    @Type(JsonType::class)
    @Column(nullable = false, columnDefinition = "json")
    var payload: Map<String, Any> = emptyMap(),
    @Column(nullable = false)
    var isActive: Boolean = true,
) : BaseEntity() {
    override fun equals(other: Any?) = entityEquals(other, TokenJpaEntity::id)

    override fun hashCode() = entityHashCode(TokenJpaEntity::id)

    override fun toString() = entityToString(*toStringProperties)

    companion object {
        val toStringProperties = arrayOf(TokenJpaEntity::id, TokenJpaEntity::token, TokenJpaEntity::type)
    }
}
