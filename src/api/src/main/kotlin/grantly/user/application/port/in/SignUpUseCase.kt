package grantly.user.application.port.`in`

import grantly.common.annotations.UseCase
import grantly.user.domain.User

@UseCase
interface SignUpUseCase {
    fun signUp(
        email: String,
        password: String,
        name: String,
    ): User
}
