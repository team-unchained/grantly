package grantly.service.adapter.out

import grantly.common.entity.IsMapper
import grantly.service.domain.Service
import org.springframework.stereotype.Component

@Component
class ServiceMapper : IsMapper<ServiceJpaEntity, Service> {
    override fun toDomain(entity: ServiceJpaEntity): Service =
        Service(
            id = entity.id ?: 0L,
            slug = entity.slug,
            name = entity.name,
            imageUrl = entity.imageUrl,
            description = entity.description,
            ownerId = entity.ownerId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: Service): ServiceJpaEntity =
        ServiceJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            slug = domain.slug,
            name = domain.name,
            imageUrl = domain.imageUrl,
            description = domain.description,
            ownerId = domain.ownerId,
        )
}
