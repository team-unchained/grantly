package grantly.member.application.port.`in`

import grantly.member.application.port.`in`.dto.LogoutParams

interface LogoutUseCase {
    fun logout(params: LogoutParams)
}
