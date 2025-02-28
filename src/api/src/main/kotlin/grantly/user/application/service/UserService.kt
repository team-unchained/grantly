package grantly.user.application.service

import grantly.common.annotations.UseCase
import grantly.user.application.port.`in`.EditProfileUseCase
import grantly.user.application.port.`in`.FindUserQuery
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.application.port.out.UserRepository
import grantly.user.domain.User

@UseCase
class UserService(
    private val userRepository: UserRepository,
) : SignUpUseCase,
    FindUserQuery,
    EditProfileUseCase {
    override fun signUp(params: SignUpParams): User {
        val user = User(email = params.email, name = params.name, password = params.password)
        user.hashPassword()
        return userRepository.createUser(user)
    }

    override fun findUserById(id: Long): User {
        val user =
            try {
                userRepository.getUser(id)
            } catch (e: RuntimeException) {
                throw Exception("User not found")
            }
        return user
    }

    override fun findAllUsers(): List<User> = userRepository.getAllUsers()

    override fun update(
        id: Long,
        name: String,
    ): User = userRepository.updateUser(id, name)
}
