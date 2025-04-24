package grantly.config

import grantly.user.application.port.out.AuthSessionRepository
import grantly.user.application.port.out.UserRepository
import grantly.user.domain.AuthSession
import grantly.user.domain.User
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.time.OffsetDateTime

class WithSessionSecurityContextFactory(
    private val userRepository: UserRepository,
    private val authSessionRepository: AuthSessionRepository,
) : WithSecurityContextFactory<WithTestSessionUser> {
    override fun createSecurityContext(annotation: WithTestSessionUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        // 이미 존재하는 유저인지 확인
        var user: User
        var authSession: AuthSession
        try {
            val existingUser = userRepository.getUserByEmail(annotation.email)
            user = existingUser
            // 세션이 존재하는지 확인
            try {
                authSession = authSessionRepository.getSessionByUserId(user.id)
            } catch (e: EntityNotFoundException) {
                // 세션이 존재하지 않으면 새로 생성
                val newAuthSession =
                    AuthSession(
                        userId = user.id,
                        token = "testToken",
                        expiresAt = OffsetDateTime.now().plusDays(1),
                        deviceId = "testDeviceId",
                    )
                authSessionRepository.createSession(newAuthSession)
                authSession = newAuthSession
            }
        } catch (e: EntityNotFoundException) {
            val (newUser, newAuthSession) = createUserAndSession(annotation)
            user = newUser
            authSession = newAuthSession
        }

        // context 에 설정
        context.authentication =
            UsernamePasswordAuthenticationToken(
                AuthenticatedUser(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                ),
                authSession.token, // 임시로 세션 토큰을 credentials 에 넣어줌
                emptyList(),
            )
        return context
    }

    private fun createUserAndSession(mockUser: WithTestSessionUser): Pair<User, AuthSession> {
        val now = OffsetDateTime.now()
        val user =
            User(
                name = mockUser.name,
                email = mockUser.email,
                password = "test123!",
                createdAt = now,
                modifiedAt = now,
                lastLoginAt = now,
            )
        val createdUser = userRepository.createUser(user)
        val authSession =
            AuthSession(
                userId = createdUser.id,
                token = "testToken",
                expiresAt = OffsetDateTime.now().plusDays(1),
                deviceId = "testDeviceId",
            )
        val createdSession = authSessionRepository.createSession(authSession)
        return Pair(createdUser, createdSession)
    }
}
