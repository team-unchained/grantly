package grantly.user.application.port.`in`

import grantly.user.domain.User

interface FindUserQuery {
    fun findUserById(id: Long): User

    fun findUserByEmail(email: String): User

    fun findAllUsers(): List<User>
}
