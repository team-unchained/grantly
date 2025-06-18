package grantly.app.adapter.out

import grantly.app.application.port.out.AppRepository
import grantly.app.domain.AppDomain
import grantly.common.annotations.PersistenceAdapter
import jakarta.persistence.EntityNotFoundException

@PersistenceAdapter
class AppPersistenceAdapter(
    private val appJpaRepository: AppJpaRepository,
    private val appMapper: AppMapper,
) : AppRepository {
    override fun getAppById(id: Long): AppDomain {
        val appEntity = appJpaRepository.findByIdAndIsActiveIsTrue(id)
        if (appEntity.isEmpty) {
            throw EntityNotFoundException("Service not found with id: $id")
        }
        return appMapper.toDomain(appEntity.get())
    }

    override fun getAppsByOwnerId(memberId: Long): List<AppDomain> {
        val appEntities = appJpaRepository.findByOwnerIdAndIsActiveIsTrueOrderByIdDesc(memberId)
        if (appEntities.isEmpty) {
            return emptyList()
        }
        return appEntities.get().map { appMapper.toDomain(it) }
    }

    override fun createApp(appDomain: AppDomain): AppDomain {
        val newService = appDomain.copy(id = 0L)
        val appEntity = appJpaRepository.save(appMapper.toEntity(newService))
        return appMapper.toDomain(appEntity)
    }

    override fun updateApp(appDomain: AppDomain): AppDomain {
        val appEntity = appJpaRepository.save(appMapper.toEntity(appDomain))
        return appMapper.toDomain(appEntity)
    }

    override fun getActiveAppCountByOwnerId(ownerId: Long) = appJpaRepository.countByIsActiveIsTrueAndOwnerId(ownerId)
}
