package grantly.user.application.service

import grantly.common.annotations.UseCase
import grantly.common.constants.AuthConstants
import grantly.user.application.port.`in`.EditProfileUseCase
import grantly.user.application.port.`in`.FindUserQuery
import grantly.user.application.port.`in`.LoginUseCase
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.LoginParams
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.application.port.out.AuthSessionRepository
import grantly.user.application.port.out.UserRepository
import grantly.user.application.service.exceptions.DuplicateEmailException
import grantly.user.application.service.exceptions.PasswordMismatchException
import grantly.user.application.service.exceptions.TokenGenerationException
import grantly.user.domain.AuthSession
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.OffsetDateTime
import java.util.UUID

private val log = KotlinLogging.logger {}

@UseCase
class UserService(
    private val userRepository: UserRepository,
    private val authSessionRepository: AuthSessionRepository,
    private val passwordEncoder: PasswordEncoder,
) : SignUpUseCase,
    LoginUseCase,
    FindUserQuery,
    EditProfileUseCase {
    override fun signUp(params: SignUpParams): User {
        try {
            findUserByEmail(params.email)
            throw DuplicateEmailException()
        } catch (e: EntityNotFoundException) {
            // 유저 생성
            val user = User(email = params.email, name = params.name, password = params.password)
            user.hashPassword(passwordEncoder)
            return userRepository.createUser(user)
        }
    }

    override fun login(params: LoginParams): AuthSession {
        val user = findUserByEmail(params.email)
        if (!user.checkPassword(passwordEncoder, params.password)) {
            throw PasswordMismatchException()
        }

        var validSession: AuthSession
        // 이미 존재하는 세션이 있는지 확인
        try {
            validSession = authSessionRepository.getSessionByUserId(user.id)
            if (!validSession.isValid()) {
                throw EntityNotFoundException("Valid session not found")
            }
        } catch (e: EntityNotFoundException) {
            // 세션이 없으면 새로 생성
            validSession = generateSessionToken(user.id, params.ip, params.userAgent)
        }
        return validSession
    }

    fun generateSessionToken(
        userId: Long,
        ip: String?,
        userAgent: String?,
    ): AuthSession {
        val retries = 3
        for (i in 1..retries) {
            val token = UUID.randomUUID().toString()
            val session =
                AuthSession(
                    userId = userId,
                    ip = ip,
                    userAgent = userAgent,
                    token = token,
                    expiresAt = OffsetDateTime.now().plusSeconds(AuthConstants.SESSION_TOKEN_EXPIRATION),
                )
            try {
                return authSessionRepository.createAuthSession(session)
            } catch (e: DataIntegrityViolationException) {
                if (i == retries) {
                    log.error { "Failed to generate session token" }
                    throw TokenGenerationException()
                } else {
                    continue
                }
            }
        }
        throw TokenGenerationException() // make compiler happy
    }

    override fun findUserById(id: Long) = userRepository.getUser(id)

    override fun findUserByEmail(email: String): User = userRepository.getUserByEmail(email)

    override fun findAllUsers(): List<User> = userRepository.getAllUsers()

    override fun update(
        id: Long,
        name: String,
    ): User = userRepository.updateUser(id, name)
}
