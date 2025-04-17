package grantly.user.application.service

import grantly.common.annotations.UseCase
import grantly.user.application.port.`in`.EditProfileUseCase
import grantly.user.application.port.`in`.FindUserQuery
import grantly.user.application.port.`in`.LoginUseCase
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.LoginParams
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.application.port.out.UserRepository
import grantly.user.application.service.exceptions.DuplicateEmailException
import grantly.user.application.service.exceptions.PasswordMismatchException
import grantly.user.domain.AuthSession
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

@UseCase
class UserService(
    private val userRepository: UserRepository,
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

        val httpSession = sessionService.getHttpSession(params.request) ?: throw EntityNotFoundException("Session not found")
        val authSession = sessionService.findSessionByToken(httpSession.token)

        if (!authSession.isValid()) {
            sessionService.delete(authSession.id)
            throw EntityNotFoundException("Valid session not found")
        }

        // 익명 세션으로 요청이 들어온 경우, 현재 유저와 연결된 세션이 있는지 확인
        if (authSession.isAnonymous()) {
            try {
                val existingUserSession = sessionService.findSessionByUserId(user.id)
                // 이미 로그인된 세션이 있는 경우 제거함
                sessionService.delete(existingUserSession.id)
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

        val updatedSession = sessionService.update(authSession)

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

    override fun findUserById(id: Long) = userRepository.getUser(id)

    override fun findUserByEmail(email: String): User = userRepository.getUserByEmail(email)

    override fun findAllUsers(): List<User> = userRepository.getAllUsers()

    override fun update(
        id: Long,
        name: String,
    ): User = userRepository.updateUser(id, name)
}
