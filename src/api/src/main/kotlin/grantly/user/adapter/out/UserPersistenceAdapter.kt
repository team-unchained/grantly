package grantly.user.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.user.application.port.out.UserRepository
import grantly.user.domain.User

@PersistenceAdapter
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val userMapper: UserMapper,
) : UserRepository {
    override fun createUser(user: User): User {
        val userEntity = userJpaRepository.save(userMapper.toEntity(user))
        return userMapper.toDomain(userEntity)
    }

    override fun getUser(id: Long): User {
        val userEntity = userJpaRepository.findById(id)
        if (userEntity.isEmpty) {
            throw RuntimeException("User not found")
        }
        return userMapper.toDomain(userEntity.get())
    }

    override fun getAllUsers(): List<User> {
        val users = userJpaRepository.findAll()
        return users.map { userMapper.toDomain(it) }
    }

    override fun updateUser(
        userId: Long,
        name: String,
    ): User {
        val optionalUser = userJpaRepository.findById(userId)
        if (optionalUser.isEmpty) {
            throw RuntimeException("User not found")
        }
        var userEntity = optionalUser.get()
        userEntity.name = name
        userEntity = userJpaRepository.save(userEntity)
        return userMapper.toDomain(userEntity)
    }
}
