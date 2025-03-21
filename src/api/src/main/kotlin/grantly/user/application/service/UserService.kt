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
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
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
            val token = generateToken()
            val csrfToken = UUID.randomUUID().toString()
            val session =
                AuthSession(
                    userId = user.id,
                    ip = params.ip,
                    userAgent = params.userAgent,
                    token = token,
                    csrfToken = csrfToken,
                    expiresAt = OffsetDateTime.now().plusSeconds(AuthConstants.TOKEN_EXPIRATION),
                )
            validSession = authSessionRepository.createAuthSession(session)
        }
        setSession(response, validSession)
        return validSession
    }

    fun generateToken(): String {
        val retries = 3
        for (i in 1..retries) {
            val token = UUID.randomUUID().toString()
            try {
                authSessionRepository.getSessionByToken(token)
            } catch (e: EntityNotFoundException) {
                return token
            }
        }

        log.error { "Failed to generate token" }
        throw TokenGenerationException()
    }

    fun setSession(
        response: HttpServletResponse,
        session: AuthSession,
    ) {
        val secondsBeforeExpiration = Duration.between(OffsetDateTime.now(), session.expiresAt).seconds
        // 세션 쿠키 설정
        val cookie =
            Cookie(AuthConstants.SESSION_COOKIE_NAME, session.token).apply {
                maxAge = secondsBeforeExpiration.toInt()
                domain = cookieDomain
                secure = true
                isHttpOnly = true
                setAttribute("SameSite", "Strict")
            }
        response.addCookie(cookie)
        // CSRF 토큰 쿠키 설정
        val csrfCookie =
            Cookie(AuthConstants.CSRF_COOKIE_NAME, session.csrfToken).apply {
                maxAge = secondsBeforeExpiration.toInt()
                domain = cookieDomain
                secure = true
                isHttpOnly = false
                setAttribute("SameSite", "Strict")
            }
        response.addCookie(csrfCookie)
    }

    override fun findUserById(id: Long) = userRepository.getUser(id)

    override fun findUserByEmail(email: String): User = userRepository.getUserByEmail(email)

    override fun findAllUsers(): List<User> = userRepository.getAllUsers()

    override fun update(
        id: Long,
        name: String,
    ): User = userRepository.updateUser(id, name)
}
