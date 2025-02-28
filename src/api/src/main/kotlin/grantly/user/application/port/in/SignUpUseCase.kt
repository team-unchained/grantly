package grantly.user.application.port.`in`

import grantly.common.annotations.UseCase
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.domain.User

@UseCase
interface SignUpUseCase {
    fun signUp(params: SignUpParams): User
}
