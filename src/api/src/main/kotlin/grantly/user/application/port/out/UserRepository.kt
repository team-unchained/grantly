package grantly.user.application.port.out

import grantly.user.domain.User

interface UserRepository {
    fun createUser(user: User): User

    fun getUser(id: Long): User

    fun getUserByEmail(email: String): User

    fun getAllUsers(): List<User>

    fun updateUser(user: User): User
}
