package grantly.member.adapter.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import grantly.app.application.port.out.AppRepository
import grantly.common.constants.AuthConstants
import grantly.member.adapter.`in`.dto.LoginRequest
import grantly.member.adapter.`in`.dto.SignUpRequest
import grantly.member.application.port.out.MemberRepository
import grantly.member.domain.MemberDomain
import grantly.session.application.port.out.AuthSessionRepository
import grantly.session.domain.AuthSessionDomain
import grantly.session.domain.SubjectType
import grantly.token.adapter.out.enums.TokenType
import grantly.token.application.port.out.TokenRepository
import grantly.token.domain.TokenDomain
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.Cookie
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.OffsetDateTime
import java.util.UUID

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerTest(
    @Autowired
    private val memberRepository: MemberRepository,
    @Autowired
    private val authSessionRepository: AuthSessionRepository,
    @Autowired
    private val tokenRepository: TokenRepository,
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val objectMapper: ObjectMapper,
    @Autowired
    private val passwordEncoder: BCryptPasswordEncoder,
    @Autowired
    private val appRepository: AppRepository,
) {
    companion object {
        @Container
        val redis =
            GenericContainer(DockerImageName.parse("redis:7"))
                .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun overrideRedisProperties(registry: DynamicPropertyRegistry) {
            redis.start()
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
        }
    }

    private lateinit var existingMember: MemberDomain

    @BeforeAll
    fun createTestMember() {
        existingMember = createTestMember("test@email.com", "test", "test123!")
    }

    @AfterEach
    fun deleteSessionData() {
        try {
            val session = authSessionRepository.getSessionByMemberId(existingMember.id)
            authSessionRepository.deleteSession(session.id)
        } catch (e: EntityNotFoundException) {
            // do nothing
        }
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 불가")
    fun `should return 409 when email is already taken`() {
        // when & then
        val jsonBody = objectMapper.writeValueAsString(SignUpRequest(existingMember.email, "test2", "test123!"))
        mockMvc
            .perform(
                post("/admin/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isConflict)
    }

    @Test
    @DisplayName("회원가입 유효성 검사 실패: 이메일 형식이 아닌 경우")
    fun `should return 422 when email is invalid`() {
        // given
        val jsonBody = objectMapper.writeValueAsString(SignUpRequest("invalid-email", "test", "test123!"))

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isUnprocessableEntity)
    }

    @Test
    @DisplayName("로그인 성공")
    fun `should return 204 when login is successful`() {
        // given
        val session = createAnonymousAuthSession()
        val jsonBody = objectMapper.writeValueAsString(LoginRequest(existingMember.email, "test123!"))

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/login")
                    .cookie(Cookie(AuthConstants.SESSION_COOKIE_NAME, session.token))
                    .cookie(Cookie(AuthConstants.DEVICE_ID_COOKIE_NAME, session.deviceId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isNoContent)
            .andExpect(cookie().exists(AuthConstants.SESSION_COOKIE_NAME))
            .andExpect(cookie().exists(AuthConstants.DEVICE_ID_COOKIE_NAME))

        assertThatCode {
            authSessionRepository.getSessionByMemberId(existingMember.id)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("로그인 성공: 익명 세션을 사용했으나 유저와 연결된 세션이 존재했을 때 이를 제거해야함")
    fun `should delete existing member session on new login request`() {
        // given
        val jsonBody = objectMapper.writeValueAsString(LoginRequest(existingMember.email, "test123!"))
        val memberSession = createMemberAuthSession(existingMember.id)
        val anonSession = createAnonymousAuthSession()

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie(
                        Cookie(AuthConstants.DEVICE_ID_COOKIE_NAME, anonSession.deviceId),
                        Cookie(AuthConstants.SESSION_COOKIE_NAME, anonSession.token),
                    ).content(jsonBody),
            ).andExpect(status().isNoContent)
            .andExpect { result ->
                val cookie = result.response.getCookie(AuthConstants.SESSION_COOKIE_NAME)
                assertThat(cookie?.value).isNotEqualTo(anonSession.token)
                assertThatException()
                    .isThrownBy { authSessionRepository.getSessionByToken(memberSession.token) }
                    .isInstanceOf(EntityNotFoundException::class.java)
            }
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호 불일치")
    fun `should return 401 when password is incorrect`() {
        // given
        val jsonBody = objectMapper.writeValueAsString(LoginRequest(existingMember.email, "wrong-password"))

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 유저")
    fun `should return 401 when member does not exist`() {
        // given
        val jsonBody = objectMapper.writeValueAsString(LoginRequest("invalid@email.com", "somepassword1234!"))

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("CSRF 토큰 조회")
    fun `should set CSRF token in cookie when requested`() {
        // when & then
        mockMvc
            .perform(
                get("/admin/v1/auth/csrf-token"),
            ).andExpect(status().isNoContent)
            .andExpect(cookie().exists(AuthConstants.CSRF_COOKIE_NAME))
    }

    @Test
    @DisplayName("CSRF 토큰 조회: 세션 토큰이 함께 들어왔을 때")
    fun `skip persist if session exists when requesting csrf token`() {
        // given
        val authSession = createMemberAuthSession(existingMember.id)

        // when & then
        mockMvc
            .perform(
                get("/admin/v1/auth/csrf-token")
                    .cookie(Cookie(AuthConstants.SESSION_COOKIE_NAME, authSession.token)),
            ).andExpect(status().isNoContent)
            .andExpect(cookie().exists(AuthConstants.CSRF_COOKIE_NAME))
    }

    @Test
    @DisplayName("로그아웃")
    fun `should delete all cookies on logout`() {
        // given
        val authSession = createMemberAuthSession(existingMember.id)

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/logout")
                    .cookie(Cookie(AuthConstants.SESSION_COOKIE_NAME, authSession.token)),
            ).andExpect(status().isNoContent)
            .andExpect { result ->
                val deletedCookie = result.response.getCookie(AuthConstants.SESSION_COOKIE_NAME)
                assertThat(deletedCookie?.value).isEqualTo("")
                assertThat(deletedCookie?.maxAge).isEqualTo(0)
            }
    }

    @Test
    @DisplayName("존재하는 유저에 대한 이메일 전송 요청")
    fun `should return 204 when email is sent successfully`() {
        // given
        val jsonBody =
            objectMapper.writeValueAsString(
                mapOf(
                    "email" to existingMember.email,
                ),
            )

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/request-password-reset")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("존재하지 않는 유저에 대한 이메일 전송 요청")
    fun `should return 204 when email does not exist`() {
        // given
        val jsonBody =
            objectMapper.writeValueAsString(
                mapOf(
                    "email" to "unknown@email.com",
                ),
            )

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/request-password-reset")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("비밀번호 초기화")
    fun `should return 204 when password reset is successful`() {
        // given
        val newMember = createTestMember("test3@email.com", "test3", "password123!")
        val tokenValue = "somerandomtoken1234"
        val newPwd = "newpassword1234!"
        val token =
            tokenRepository.create(
                TokenDomain(
                    token = tokenValue,
                    expiresAt = OffsetDateTime.now().plusHours(1),
                    type = TokenType.PASSWORD_RESET,
                    payload = mapOf("memberId" to newMember.id),
                ),
            )
        val jsonBody =
            objectMapper.writeValueAsString(
                mapOf(
                    "token" to token.token,
                    "password" to newPwd,
                ),
            )

        // when & then
        mockMvc
            .perform(
                post("/admin/v1/auth/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isNoContent)

        val usedToken = tokenRepository.get(tokenValue)
        val updatedMember = memberRepository.getMember(newMember.id)
        assertThat(usedToken.isActive).isFalse
        assertThat(updatedMember.checkPassword(passwordEncoder, newPwd)).isTrue
    }

    fun createTestMember(
        email: String,
        name: String,
        password: String,
    ): MemberDomain {
        val member = MemberDomain(email = email, name = name, password = password)
        member.hashPassword(passwordEncoder)
        return memberRepository.createMember(member)
    }

    fun createAnonymousAuthSession(): AuthSessionDomain {
        val session =
            AuthSessionDomain(
                subjectType = SubjectType.MEMBER,
                token = UUID.randomUUID().toString(),
                deviceId = UUID.randomUUID().toString(),
                expiresAt = OffsetDateTime.now().plusSeconds(3600),
            )
        return authSessionRepository.createSession(session)
    }

    fun createMemberAuthSession(memberId: Long): AuthSessionDomain {
        val session =
            AuthSessionDomain(
                token = UUID.randomUUID().toString(),
                deviceId = UUID.randomUUID().toString(),
                expiresAt = OffsetDateTime.now().plusSeconds(3600),
                subjectId = memberId,
                subjectType = SubjectType.MEMBER,
            )
        return authSessionRepository.createSession(session)
    }
}
