package grantly.app.application.port.`in`

import grantly.app.application.port.`in`.dto.UpdateAppClientParams
import grantly.app.domain.AppClientDomain

interface UpdateAppClientUseCase {
    fun updateAppClient(params: UpdateAppClientParams): AppClientDomain
}
