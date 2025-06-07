package grantly.service.adapter.out

import grantly.common.entity.IsMapper
import grantly.service.domain.ServiceDomain
import org.springframework.stereotype.Component

@Component
class ServiceMapper : IsMapper<ServiceJpaEntity, ServiceDomain> {
    override fun toDomain(entity: ServiceJpaEntity): ServiceDomain =
        ServiceDomain(
            id = entity.id ?: 0L,
            slug = entity.slug,
            name = entity.name,
            imageUrl = entity.imageUrl,
            description = entity.description,
            ownerId = entity.ownerId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )

    override fun toEntity(domain: ServiceDomain): ServiceJpaEntity =
        ServiceJpaEntity(
            id = if (domain.id == 0L) null else domain.id,
            slug = domain.slug,
            name = domain.name,
            imageUrl = domain.imageUrl,
            description = domain.description,
            ownerId = domain.ownerId,
            isActive = domain.isActive,
        )
}
