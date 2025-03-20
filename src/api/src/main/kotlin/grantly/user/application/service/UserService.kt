package grantly.user.application.service

import grantly.common.annotations.UseCase
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
import grantly.user.domain.AuthSession
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
import java.time.OffsetDateTime

@UseCase
class UserService(
    private val userRepository: UserRepository,
    private val authSessionRepository: AuthSessionRepository,
    private val passwordEncoder: PasswordEncoder,
) : SignUpUseCase,
    LoginUseCase,
    FindUserQuery,
    EditProfileUseCase {
    @Value("\${grantly.cookie.domain}")
    private lateinit var cookieDomain: String

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

    override fun login(
        params: LoginParams,
        response: HttpServletResponse,
    ): AuthSession {
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
            var session = AuthSession(userId = user.id, ip = params.ip, userAgent = params.userAgent)
            while (true) {
                try {
                    validSession = authSessionRepository.createAuthSession(session)
                    break
                } catch (e: DataIntegrityViolationException) {
                    session.generateToken()
                }
            }
        }
        return validSession
    }

    override fun setSessionCookie(
        response: HttpServletResponse,
        session: AuthSession,
    ) {
        val cookie = Cookie("session_token", session.token)
        var secondsBeforeExpiration = Duration.between(OffsetDateTime.now(), session.expiresAt).seconds
        cookie.maxAge = secondsBeforeExpiration.toInt()
        cookie.domain = cookieDomain
        cookie.secure = true
        response.addCookie(cookie)
    }

    override fun findUserById(id: Long) = userRepository.getUser(id)

    override fun findUserByEmail(email: String): User = userRepository.getUserByEmail(email)

    override fun findAllUsers(): List<User> = userRepository.getAllUsers()

    override fun update(
        id: Long,
        name: String,
    ): User = userRepository.updateUser(id, name)
}
