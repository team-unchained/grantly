package grantly.app.application.port.`in`

import grantly.app.application.port.`in`.dto.UpdateAppParams
import grantly.app.domain.AppDomain

interface UpdateAppUseCase {
    fun updateApp(params: UpdateAppParams): AppDomain
}
