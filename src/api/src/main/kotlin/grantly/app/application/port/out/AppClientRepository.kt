package grantly.app.application.port.out

import grantly.app.domain.AppClientDomain

interface AppClientRepository {
    fun createAppClient(appClient: AppClientDomain): AppClientDomain

    fun findByClientId(clientId: String): AppClientDomain

    fun findByAppIdAndClientId(
        appId: Long,
        clientId: String,
    ): AppClientDomain

    fun findByAppId(appId: Long): List<AppClientDomain>

    fun delete(id: Long)
}
