package grantly.user.application.port.`in`

import grantly.common.annotations.UseCase
import grantly.user.domain.User

@UseCase
interface EditProfileUseCase {
    fun update(
        id: Long,
        name: String,
    ): User
}
