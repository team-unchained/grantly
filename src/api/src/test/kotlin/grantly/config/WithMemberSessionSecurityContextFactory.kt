package grantly.config

import grantly.member.application.port.out.MemberRepository
import grantly.member.domain.MemberDomain
import grantly.session.application.port.out.AuthSessionRepository
import grantly.session.domain.AuthSessionDomain
import grantly.session.domain.SubjectType
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.time.OffsetDateTime

@TestComponent
class WithMemberSessionSecurityContextFactory(
    @Autowired
    private val memberRepository: MemberRepository,
    @Autowired
    private val authSessionRepository: AuthSessionRepository,
) : WithSecurityContextFactory<WithTestSessionMember> {
    override fun createSecurityContext(annotation: WithTestSessionMember): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        // 이미 존재하는 유저인지 확인
        var member: MemberDomain
        var authSession: AuthSessionDomain
        try {
            val existingMember = memberRepository.getMemberByEmail(annotation.email)
            member = existingMember
            // 세션이 존재하는지 확인
            try {
                authSession = authSessionRepository.getSessionByMemberId(member.id)
            } catch (e: EntityNotFoundException) {
                // 세션이 존재하지 않으면 새로 생성
                val newAuthSession =
                    AuthSessionDomain(
                        token = "testToken",
                        expiresAt = OffsetDateTime.now().plusDays(1),
                        deviceId = "testDeviceId",
                    )
                newAuthSession.connectMember(member.id)
                authSessionRepository.createSession(newAuthSession)
                authSession = newAuthSession
            }
        } catch (e: EntityNotFoundException) {
            val (newMember, newAuthSession) = createMemberAndSession(annotation)
            member = newMember
            authSession = newAuthSession
        }

        // 세션 토큰을 ThreadLocal 에 저장
        TestSessionTokenHolder.set(authSession.token)

        // context 에 설정
        val requestEntity =
            AuthenticationEntity(
                id = member.id,
                name = member.name,
                email = member.email,
                role = SubjectType.MEMBER,
            )
        context.authentication =
            UsernamePasswordAuthenticationToken(
                requestEntity,
                authSession.token, // 임시로 세션 토큰을 credentials 에 넣어줌
                requestEntity.authorities,
            )
        return context
    }

    private fun createMemberAndSession(mockMember: WithTestSessionMember): Pair<MemberDomain, AuthSessionDomain> {
        val now = OffsetDateTime.now()
        val member =
            MemberDomain(
                name = mockMember.name,
                email = mockMember.email,
                password = "test123!",
                createdAt = now,
                modifiedAt = now,
                lastLoginAt = now,
            )
        val createdMember = memberRepository.createMember(member)
        val authSession =
            AuthSessionDomain(
                token = "testToken",
                expiresAt = OffsetDateTime.now().plusDays(1),
                deviceId = "testDeviceId",
            )
        authSession.connectMember(createdMember.id)
        val createdSession = authSessionRepository.createSession(authSession)
        return Pair(createdMember, createdSession)
    }
}
