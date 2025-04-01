package grantly.user.application.port.`in`

import grantly.user.domain.User

interface EditProfileUseCase {
    fun update(
        id: Long,
        name: String,
    ): User
}
