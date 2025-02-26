package grantly.user.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.user.application.port.out.UserRepository
import grantly.user.domain.User

@PersistenceAdapter
class UserPersistenceAdapter(
    private val springDataUserRepository: SpringDataUserRepository,
    private val userMapper: UserMapper,
) : UserRepository {
    override fun createUser(
        email: String,
        password: String,
        name: String,
    ): User {
        val userEntity = springDataUserRepository.save(UserJpaEntity(email = email, password = password, name = name))
        return userMapper.toDomain(userEntity)
    }

    override fun getUser(id: Long): User {
        val userEntity = springDataUserRepository.findById(id)
        if (userEntity.isEmpty) {
            throw RuntimeException("User not found")
        }
        return userMapper.toDomain(userEntity.get())
    }

    override fun getAllUsers(): List<User> {
        val users = springDataUserRepository.findAll()
        return users.map { userMapper.toDomain(it) }
    }

    override fun updateUser(
        userId: Long,
        name: String,
    ): User {
        val optionalUser = springDataUserRepository.findById(userId)
        if (optionalUser.isEmpty) {
            throw RuntimeException("User not found")
        }
        var userEntity = optionalUser.get()
        userEntity.name = name
        userEntity = springDataUserRepository.save(userEntity)
        return userMapper.toDomain(userEntity)
    }
}
