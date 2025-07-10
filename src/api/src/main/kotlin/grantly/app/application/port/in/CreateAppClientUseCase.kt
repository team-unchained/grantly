package grantly.app.application.port.`in`

import grantly.app.application.port.`in`.dto.CreateAppClientParams
import grantly.app.domain.AppClientDomain

interface CreateAppClientUseCase {
    fun createAppClient(params: CreateAppClientParams): AppClientDomain
}
