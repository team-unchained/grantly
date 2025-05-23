package grantly.user.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import grantly.common.annotations.UseCase
import grantly.common.core.email.EmailSender
import grantly.config.CustomHttpSession
import grantly.token.adapter.out.dto.UserIdTokenPayload
import grantly.token.application.service.TokenService
import grantly.token.application.service.exceptions.InvalidTokenException
import grantly.token.domain.Token
import grantly.user.application.port.`in`.EditProfileUseCase
import grantly.user.application.port.`in`.FindUserQuery
import grantly.user.application.port.`in`.LoginUseCase
import grantly.user.application.port.`in`.LogoutUseCase
import grantly.user.application.port.`in`.PasswordResetUseCase
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.LoginParams
import grantly.user.application.port.`in`.dto.LogoutParams
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.application.port.out.UserRepository
import grantly.user.application.service.exceptions.DuplicateEmailException
import grantly.user.application.service.exceptions.PasswordMismatchException
import grantly.user.domain.AuthSession
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder

@UseCase
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val sessionService: SessionService,
    private val tokenService: TokenService,
    private val emailSender: EmailSender,
    private val objectMapper: ObjectMapper,
) : SignUpUseCase,
    LoginUseCase,
    LogoutUseCase,
    FindUserQuery,
    EditProfileUseCase,
    PasswordResetUseCase {
    @Value("\${grantly.service.domain}")
    private lateinit var serviceDomain: String

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
        // 유저와 연결 및 저장
        authSession.connectUser(user.id)
        val updatedSession = sessionService.update(authSession)

        // 쿠키 설정
        sessionService.setHttpSession(
            params.request,
            CustomHttpSession(updatedSession.token, updatedSession.expiresAt, updatedSession.deviceId),
        )
        sessionService.setCookies(params.request, params.response)
        return updatedSession
    }

    override fun logout(params: LogoutParams) {
        // 세션 삭제
        val httpSession =
            sessionService.getHttpSession(params.request)
        val authSession = sessionService.findSessionByToken(httpSession.token)
        sessionService.delete(authSession.id)

        sessionService.unsetCookies(params.response)
    }

    override fun findUserById(id: Long) = userRepository.getUser(id)

    override fun findUserByEmail(email: String): User = userRepository.getUserByEmail(email)

    override fun findAllUsers(): List<User> = userRepository.getAllUsers()

    override fun update(
        id: Long,
        name: String,
    ): User {
        val user = findUserById(id)
        user.name = name
        return userRepository.updateUser(user)
    }

    override fun requestPasswordReset(email: String): Boolean {
        // 존재하는 유저인지 확인
        val user =
            try {
                findUserByEmail(email)
            } catch (e: EntityNotFoundException) {
                return false
            }
        // 비밀번호 리셋 토큰 생성
        val token = tokenService.createPasswordResetToken(user.id)
        // 이메일 전송
        emailSender.send(
            user.email,
            "Password Reset",
            "$serviceDomain/auth/reset-password?token=${token.token}",
        )
        return true
    }

    override fun resetPassword(
        token: String,
        newPassword: String,
    ) {
        val tokenObj: Token
        try {
            tokenObj = tokenService.findToken(token)
            if (!tokenObj.isValid()) {
                throw InvalidTokenException()
            }
        } catch (e: EntityNotFoundException) {
            throw InvalidTokenException()
        }

        val payload = tokenObj.getPayloadAs<UserIdTokenPayload>(objectMapper)
        val userId = payload.userId

        val user =
            runCatching {
                findUserById(userId)
            }.getOrElse { e ->
                when (e) {
                    // 토큰에 연결된 유저가 존재하지 않는 경우, 토큰이 유효하지 않은 것처럼 처리함
                    is EntityNotFoundException -> throw InvalidTokenException()
                    else -> throw e
                }
            }

        // 비번 변경
        user.resetPassword(passwordEncoder, newPassword)
        userRepository.updateUser(user)
        // 사용한 토큰 비활성화
        tokenService.deactivateToken(tokenObj)
    }
}
