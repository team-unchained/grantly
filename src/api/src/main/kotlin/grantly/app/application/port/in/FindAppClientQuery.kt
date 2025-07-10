package grantly.app.application.port.`in`

import grantly.app.application.port.`in`.dto.FindAppClientParams
import grantly.app.domain.AppClientDomain

interface FindAppClientQuery {
    fun findAppClient(params: FindAppClientParams): AppClientDomain

    fun findAppClientsByAppId(appId: Long): List<AppClientDomain>
}
