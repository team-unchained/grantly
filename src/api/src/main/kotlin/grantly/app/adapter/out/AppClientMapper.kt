package grantly.app.adapter.out

import grantly.app.domain.AppClientDomain
import grantly.common.entity.IsMapper
import grantly.oauth.adapter.out.enums.OAuthClientScope
import grantly.oauth.adapter.out.enums.OAuthGrantType
import org.springframework.stereotype.Component

@Component
class AppClientMapper : IsMapper<AppClientJpaEntity, AppClientDomain> {
    override fun toDomain(entity: AppClientJpaEntity): AppClientDomain =
        AppClientDomain(
            id = entity.id ?: 0L,
            appId = entity.appId,
            title = entity.title,
            clientId = entity.clientId,
            clientSecret = entity.clientSecret,
            redirectUris = entity.redirectUris.toMutableList(),
            scopes = entity.scopes.mapNotNull { OAuthClientScope.fromValue(it) }.toMutableList(),
            grantType = OAuthGrantType.fromValue(entity.grantType)!!,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: AppClientDomain): AppClientJpaEntity =
        AppClientJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            appId = domain.appId,
            title = domain.title,
            clientId = domain.clientId,
            clientSecret = domain.clientSecret,
            redirectUris = domain.redirectUris.toMutableList(),
            scopes = domain.scopes.map { it.value }.toMutableList(),
            grantType = domain.grantType.value,
        )
}
