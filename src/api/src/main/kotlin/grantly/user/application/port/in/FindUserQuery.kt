package grantly.user.application.port.`in`

import grantly.common.annotations.UseCase
import grantly.user.domain.User

@UseCase
interface FindUserQuery {
    fun findUserById(id: Long): User

    fun findUserByEmail(email: String): User

    fun findAllUsers(): List<User>
}
