package grantly.user.application.port.out

import grantly.user.domain.User

interface UserRepository {
    fun createUser(user: User): User

    fun getUser(id: Long): User

    fun getAllUsers(): List<User>

    fun updateUser(
        userId: Long,
        name: String,
    ): User
}
