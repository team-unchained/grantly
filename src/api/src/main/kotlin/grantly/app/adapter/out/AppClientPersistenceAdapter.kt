package grantly.app.adapter.out

import grantly.app.application.port.out.AppClientRepository
import grantly.app.domain.AppClientDomain
import grantly.common.annotations.PersistenceAdapter
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Component

@PersistenceAdapter
@Component
class AppClientPersistenceAdapter(
    private val appClientJpaRepository: AppClientJpaRepository,
    private val appClientMapper: AppClientMapper,
) : AppClientRepository {
    override fun createAppClient(appClient: AppClientDomain): AppClientDomain {
        val entity = appClientMapper.toEntity(appClient)
        val savedEntity = appClientJpaRepository.save(entity)
        return appClientMapper.toDomain(savedEntity)
    }

    override fun findByClientId(clientId: String): AppClientDomain {
        // clientId로 엔티티를 찾기 위해 모든 앱에서 검색
        val allClients = appClientJpaRepository.findAll()
        val appClientEntity =
            allClients.find { it.clientId == clientId }
                ?: throw EntityNotFoundException("OAuth client not found with clientId: $clientId")
        return appClientMapper.toDomain(appClientEntity)
    }

    override fun findByAppIdAndClientId(
        appId: Long,
        clientId: String,
    ): AppClientDomain {
        val appClientEntity = appClientJpaRepository.findByAppIdAndClientId(appId, clientId)
        if (appClientEntity.isEmpty) {
            throw EntityNotFoundException("OAuth client not found with appId: $appId, clientId: $clientId")
        }
        return appClientMapper.toDomain(appClientEntity.get())
    }

    override fun findByAppId(appId: Long): List<AppClientDomain> =
        appClientJpaRepository
            .findByAppId(appId)
            .map { appClientMapper.toDomain(it) }

    override fun delete(id: Long) {
        appClientJpaRepository.deleteById(id)
    }
}
