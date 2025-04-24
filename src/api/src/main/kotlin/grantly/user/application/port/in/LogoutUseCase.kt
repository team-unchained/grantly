package grantly.user.application.port.`in`

import grantly.user.application.port.`in`.dto.LogoutParams

interface LogoutUseCase {
    fun logout(params: LogoutParams)
}
