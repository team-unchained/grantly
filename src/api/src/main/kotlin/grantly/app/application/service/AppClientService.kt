package grantly.app.application.service

import grantly.app.application.port.`in`.CreateAppClientUseCase
import grantly.app.application.port.`in`.DeleteAppClientUseCase
import grantly.app.application.port.`in`.FindAppClientQuery
import grantly.app.application.port.`in`.UpdateAppClientUseCase
import grantly.app.application.port.`in`.dto.CreateAppClientParams
import grantly.app.application.port.`in`.dto.DeleteAppClientParams
import grantly.app.application.port.`in`.dto.FindAppClientParams
import grantly.app.application.port.`in`.dto.UpdateAppClientParams
import grantly.app.application.port.out.AppClientRepository
import grantly.app.application.port.out.AppRepository
import grantly.app.domain.AppClientDomain
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AppClientService(
    private val appClientRepository: AppClientRepository,
    private val appRepository: AppRepository,
) : CreateAppClientUseCase,
    UpdateAppClientUseCase,
    DeleteAppClientUseCase,
    FindAppClientQuery {
    override fun createAppClient(params: CreateAppClientParams): AppClientDomain {
        val clientId = generateClientId()
        val clientSecret = generateClientSecret()

        val appClient =
            AppClientDomain(
                appId = params.appId,
                title = params.title,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUris = params.redirectUris.toMutableList(),
                scopes = params.scopes.toMutableList(),
                grantType = params.grantType,
            )

        return appClientRepository.save(appClient)
    }

    override fun updateAppClient(params: UpdateAppClientParams): AppClientDomain {
        val existingClient = appClientRepository.findByAppIdAndClientId(params.appId, params.clientId)

        existingClient.title = params.title
        existingClient.redirectUris = params.redirectUris.toMutableList()
        existingClient.scopes = params.scopes.toMutableList()
        existingClient.grantType = params.grantType

        return appClientRepository.save(existingClient)
    }

    override fun deleteAppClient(params: DeleteAppClientParams) {
        val appClient = appClientRepository.findByAppIdAndClientId(params.appId, params.clientId)
        appClientRepository.delete(appClient.id)
    }

    override fun findAppClient(params: FindAppClientParams): AppClientDomain =
        appClientRepository.findByAppIdAndClientId(params.appId, params.clientId)

    override fun findAppClientsByAppId(appId: Long): List<AppClientDomain> = appClientRepository.findByAppId(appId)

    private fun generateClientId(): String = "client_${UUID.randomUUID().toString().replace("-", "")}"

    private fun generateClientSecret(): String = "secret_${UUID.randomUUID().toString().replace("-", "")}"
}
