package grantly.user.application.port.out

import grantly.user.domain.UserDomain

interface UserRepository {
    fun createUser(user: UserDomain): UserDomain

    fun getUser(id: Long): UserDomain

    fun getUserByEmail(email: String): UserDomain

    fun updateUser(user: UserDomain): UserDomain
}
