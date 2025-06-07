package grantly.member.application.port.`in`

import grantly.member.application.port.`in`.dto.LoginParams
import grantly.member.domain.AuthSessionDomain

interface LoginUseCase {
    fun login(params: LoginParams): AuthSessionDomain
}
