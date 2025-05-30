package grantly.member.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import grantly.common.annotations.UseCase
import grantly.common.core.email.EmailJobScope
import grantly.common.core.email.EmailSender
import grantly.config.CustomHttpSession
import grantly.member.application.port.`in`.EditProfileUseCase
import grantly.member.application.port.`in`.FindMemberQuery
import grantly.member.application.port.`in`.LoginUseCase
import grantly.member.application.port.`in`.LogoutUseCase
import grantly.member.application.port.`in`.PasswordResetUseCase
import grantly.member.application.port.`in`.SignUpUseCase
import grantly.member.application.port.`in`.dto.LoginParams
import grantly.member.application.port.`in`.dto.LogoutParams
import grantly.member.application.port.`in`.dto.SignUpParams
import grantly.member.application.port.out.MemberRepository
import grantly.member.application.service.exceptions.DuplicateEmailException
import grantly.member.application.service.exceptions.PasswordMismatchException
import grantly.member.domain.AuthSession
import grantly.member.domain.Member
import grantly.token.adapter.out.dto.MemberIdTokenPayload
import grantly.token.application.service.TokenService
import grantly.token.application.service.exceptions.InvalidTokenException
import grantly.token.domain.Token
import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder

@UseCase
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val sessionService: SessionService,
    private val tokenService: TokenService,
    private val emailSender: EmailSender,
    private val objectMapper: ObjectMapper,
    private val emailJobScope: EmailJobScope,
) : SignUpUseCase,
    LoginUseCase,
    LogoutUseCase,
    FindMemberQuery,
    EditProfileUseCase,
    PasswordResetUseCase {
    @Value("\${grantly.service.domain}")
    private lateinit var serviceDomain: String

    override fun signUp(params: SignUpParams): Member {
        try {
            findMemberByEmail(params.email)
            throw DuplicateEmailException()
        } catch (e: EntityNotFoundException) {
            // 멤버 생성
            val member = Member(email = params.email, name = params.name, password = params.password)
            member.hashPassword(passwordEncoder)
            return memberRepository.createMember(member)
        }
    }

    override fun login(params: LoginParams): AuthSession {
        val member = findMemberByEmail(params.email)
        if (!member.checkPassword(passwordEncoder, params.password)) {
            throw PasswordMismatchException()
        }

        val httpSession = sessionService.getHttpSession(params.request)
        val authSession = sessionService.findSessionByToken(httpSession.token)

        if (!authSession.isValid()) {
            sessionService.delete(authSession.id)
            throw EntityNotFoundException("Valid session not found")
        }

        // 익명 세션으로 요청이 들어온 경우, 현재 멤버와 연결된 세션이 있는지 확인
        if (authSession.isAnonymous()) {
            try {
                val existingmemberSession = sessionService.findSessionByMemberId(member.id)
                // 이미 로그인된 세션이 있는 경우 제거함
                sessionService.delete(existingmemberSession.id)
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
        // 멤버와 연결 및 저장
        authSession.connectMember(member.id)
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

    override fun findMemberById(id: Long) = memberRepository.getMember(id)

    override fun findMemberByEmail(email: String): Member = memberRepository.getMemberByEmail(email)

    override fun findAllMembers(): List<Member> = memberRepository.getAllMembers()

    override fun update(
        id: Long,
        name: String,
    ): Member {
        val member = findMemberById(id)
        member.name = name
        return memberRepository.updateMember(member)
    }

    override fun requestPasswordReset(email: String): Boolean {
        // 존재하는 멤버인지 확인
        val member =
            try {
                findMemberByEmail(email)
            } catch (e: EntityNotFoundException) {
                return false
            }
        // 비밀번호 리셋 토큰 생성
        val token = tokenService.createPasswordResetToken(member.id)
        // 이메일 전송
        emailJobScope.launch {
            emailSender.send(
                member.email,
                "Password Reset",
                getResetPasswordEmailParams(token)
                    .let { emailSender.buildHTML("reset-password", it) },
            )
        }
        return true
    }

    fun getResetPasswordEmailParams(token: Token): Map<String, Any> =
        mapOf(
            "resetPasswordUrl" to "$serviceDomain/auth/reset-password?token=${token.token}",
        )

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

        val payload = tokenObj.getPayloadAs<MemberIdTokenPayload>(objectMapper)
        val memberId = payload.memberId

        val member =
            runCatching {
                findMemberById(memberId)
            }.getOrElse { e ->
                when (e) {
                    // 토큰에 연결된 멤버가 존재하지 않는 경우, 토큰이 유효하지 않은 것처럼 처리함
                    is EntityNotFoundException -> throw InvalidTokenException()
                    else -> throw e
                }
            }

        // 비번 변경
        member.resetPassword(passwordEncoder, newPassword)
        memberRepository.updateMember(member)
        // 사용한 토큰 비활성화
        tokenService.deactivateToken(tokenObj)
    }
}
