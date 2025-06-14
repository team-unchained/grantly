package grantly.app.adapter.out

import grantly.app.domain.AppDomain
import grantly.common.entity.IsMapper
import org.springframework.stereotype.Component

@Component
class AppMapper : IsMapper<AppJpaEntity, AppDomain> {
    override fun toDomain(entity: AppJpaEntity): AppDomain =
        AppDomain(
            id = entity.id ?: 0L,
            slug = entity.slug,
            name = entity.name,
            imageUrl = entity.imageUrl,
            description = entity.description,
            ownerId = entity.ownerId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: AppDomain): AppJpaEntity =
        AppJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            slug = domain.slug,
            name = domain.name,
            imageUrl = domain.imageUrl,
            description = domain.description,
            ownerId = domain.ownerId,
            isActive = domain.isActive,
        )
}
