package grantly.user.application.port.`in`

import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.domain.User

interface SignUpUseCase {
    fun signUp(params: SignUpParams): User
}
