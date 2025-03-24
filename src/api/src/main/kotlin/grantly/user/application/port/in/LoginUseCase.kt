package grantly.user.application.port.`in`

import grantly.user.application.port.`in`.dto.LoginParams
import grantly.user.domain.AuthSession

interface LoginUseCase {
    fun login(params: LoginParams): AuthSession
}
