package grantly.member.application.port.`in`

import grantly.member.application.port.`in`.dto.LoginParams
import grantly.member.domain.AuthSession

interface LoginUseCase {
    fun login(params: LoginParams): AuthSession
}
