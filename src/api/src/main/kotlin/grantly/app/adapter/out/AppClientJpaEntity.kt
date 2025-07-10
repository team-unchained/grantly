package grantly.app.adapter.out

import grantly.common.entity.BaseEntity
import grantly.common.entity.entityEquals
import grantly.common.entity.entityHashCode
import grantly.common.entity.entityToString
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "app_clients")
class AppClientJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "app_id", nullable = false)
    val appId: Long,
    @Column(name = "title", nullable = false)
    var title: String,
    @Column(name = "client_id", nullable = false, unique = true)
    var clientId: String,
    @Column(name = "client_secret", nullable = false)
    var clientSecret: String,
    @ElementCollection
    @CollectionTable(name = "app_client_redirect_uris", joinColumns = [JoinColumn(name = "client_id")])
    @Column(name = "redirect_uri")
    var redirectUris: MutableList<String> = mutableListOf(),
    @ElementCollection
    @CollectionTable(name = "app_client_scopes", joinColumns = [JoinColumn(name = "client_id")])
    @Column(name = "scope")
    var scopes: MutableList<String> = mutableListOf(),
    @Column(name = "grant_type", nullable = false)
    var grantType: String,
) : BaseEntity() {
    override fun equals(other: Any?) = entityEquals(other, AppClientJpaEntity::id)

    override fun hashCode() = entityHashCode(AppClientJpaEntity::id)

    override fun toString() = entityToString(*toStringProperties)

    companion object {
        val toStringProperties = arrayOf(AppClientJpaEntity::id, AppClientJpaEntity::appId, AppClientJpaEntity::title)
    }
}
