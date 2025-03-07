package grantly.user.application.service

import grantly.common.annotations.UseCase
import grantly.user.application.port.`in`.EditProfileUseCase
import grantly.user.application.port.`in`.FindUserQuery
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.application.port.out.UserRepository
import grantly.user.application.service.exceptions.DuplicateEmailException
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException

@UseCase
class UserService(
    private val userRepository: UserRepository,
) : SignUpUseCase,
    FindUserQuery,
    EditProfileUseCase {
    override fun signUp(params: SignUpParams): User {
        try {
            findUserByEmail(params.email)
            throw DuplicateEmailException()
        } catch (e: EntityNotFoundException) {
            // 유저 생성
            val user = User(email = params.email, name = params.name, password = params.password)
            user.hashPassword()
            return userRepository.createUser(user)
        }
    }

    override fun findUserById(id: Long) = userRepository.getUser(id)

    override fun findUserByEmail(email: String): User = userRepository.getUserByEmail(email)

    override fun findAllUsers(): List<User> = userRepository.getAllUsers()

    override fun update(
        id: Long,
        name: String,
    ): User = userRepository.updateUser(id, name)
}
