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
import grantly.user.domain.AuthSession
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.OffsetDateTime
import java.util.UUID

private val log = KotlinLogging.logger {}

@UseCase
class UserService(
    private val userRepository: UserRepository,
    private val authSessionRepository: AuthSessionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val sessionService: SessionService,
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

        val httpSession = sessionService.getHttpSession(params.request)
        val authSession = authSessionRepository.getSessionByToken(httpSession.token)

        if (!authSession.isValid()) {
            authSessionRepository.deleteSession(authSession.id)
            throw EntityNotFoundException("Valid session not found")
        }

        // 익명 세션으로 요청이 들어온 경우, 현재 유저와 연결된 세션이 있는지 확인
        if (authSession.isAnonymous()) {
            try {
                val existingUserSession = authSessionRepository.getSessionByUserId(user.id)
                // 이미 로그인된 세션이 있는 경우 제거함
                authSessionRepository.deleteSession(existingUserSession.id)
            } catch (e: EntityNotFoundException) {
                // do nothing
            }
        }

        // 메타 정보 갱신
        authSession.updateMeta(
            deviceId = httpSession.deviceId,
            ip = params.request.remoteAddr,
            userAgent = params.request.getHeader("User-Agent"),
        )
        // 세션 값 갱신
        val (newSessionToken, expiresAt) = sessionService.generateSessionToken()
        authSession.replaceToken(newSessionToken, expiresAt)
        // 유저와 연결
        authSession.connectUser(user.id)

        val updatedSession = authSessionRepository.updateSession(authSession)

        // 세션 토큰을 쿠키에 설정
        sessionService.setSessionToken(
            params.response,
            updatedSession.token,
            updatedSession.expiresAt,
        )
        // 디바이스 ID를 쿠키에 설정
        sessionService.setDeviceId(params.response, updatedSession.deviceId)
        return updatedSession
    }

    /**
     * 세션 토큰 생성
     * @return 생성된 세션 토큰과 만료 시각
     */
    private fun generateToken(): Pair<String, OffsetDateTime> =
        Pair(UUID.randomUUID().toString(), OffsetDateTime.now().plusSeconds(AuthConstants.SESSION_TOKEN_EXPIRATION))

    private fun buildNewSession(
        userId: Long,
        ip: String?,
        userAgent: String?,
    ): AuthSession {
        val (token, expiresAt) = generateToken()
        return AuthSession(
            userId = userId,
            token = token,
            deviceId = UUID.randomUUID().toString(),
            ip = ip,
            userAgent = userAgent,
            expiresAt = expiresAt,
        )
    }

    /**
     * 세션 갱신.
     * 기존 세션이 유효한데 deviceId 가 다르거나 없는 경우, 기존 세션이 만료된 경우를 처리한다.
     * @return 갱신된 세션
     */
    private fun refreshSession(
        existing: AuthSession,
        deviceId: String?,
        ip: String?,
        userAgent: String?,
    ): AuthSession {
        val (token, expiresAt) = generateToken()
        return existing.copy(
            token = token,
            deviceId = deviceId ?: UUID.randomUUID().toString(),
            ip = ip,
            userAgent = userAgent,
            expiresAt = expiresAt,
        )
    }

    override fun findUserById(id: Long) = userRepository.getUser(id)

    override fun findUserByEmail(email: String): User = userRepository.getUserByEmail(email)

    override fun findAllUsers(): List<User> = userRepository.getAllUsers()

    override fun update(
        id: Long,
        name: String,
    ): User = userRepository.updateUser(id, name)
}
