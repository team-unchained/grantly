package grantly.app.application.port.`in`

import grantly.app.application.port.`in`.dto.CreateAppParams
import grantly.app.domain.AppDomain

interface CreateAppUseCase {
    fun createApp(params: CreateAppParams): AppDomain
}
