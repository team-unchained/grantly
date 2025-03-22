package grantly.user.adapter.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import grantly.common.constants.AuthConstants
import grantly.user.adapter.`in`.dto.LoginRequest
import grantly.user.adapter.`in`.dto.SignUpRequest
import grantly.user.application.port.out.UserRepository
import grantly.user.domain.User
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest(
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val objectMapper: ObjectMapper,
    @Autowired
    private val env: Environment,
    @Autowired
    private val passwordEncoder: BCryptPasswordEncoder,
) {
    companion object {
        @Container
        val redisContainer =
            GenericContainer("redis:7.0").apply {
                withExposedPorts(6379)
            }
    }

    private lateinit var existingUser: User

    @BeforeAll
    fun setUp() {
        existingUser = createTestUser("test@email.com", "test", "test123!")
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 불가")
    fun `should return 409 when email is already taken`() {
        // when & then
        val jsonBody = objectMapper.writeValueAsString(SignUpRequest(existingUser.email, "test2", "test123!"))
        mockMvc
            .perform(
                post("/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isConflict)
    }

    @Test
    @DisplayName("회원가입 성공")
    fun `should return 201 when sign up is successful`() {
        // given
        val userEmail = "test2@email.com"
        val jsonBody =
            objectMapper.writeValueAsString(SignUpRequest(userEmail, "test2", "test123!"))

        // when & then
        mockMvc
            .perform(
                post("/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.user").exists())
            .andExpect(jsonPath("$.user.email").value(userEmail))
    }

    @Test
    @DisplayName("회원가입 유효성 검사 실패: 이메일 형식이 아닌 경우")
    fun `should return 422 when email is invalid`() {
        // given
        val jsonBody = objectMapper.writeValueAsString(SignUpRequest("invalid-email", "test", "test123!"))

        // when & then
        mockMvc
            .perform(
                post("/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isUnprocessableEntity)
    }

    @Test
    @DisplayName("로그인 성공")
    fun `should return 204 when login is successful`() {
        // given
        val jsonBody = objectMapper.writeValueAsString(LoginRequest(existingUser.email, "test123!"))

        // when & then
        mockMvc
            .perform(
                post("/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isNoContent)
            .andExpect(cookie().exists(AuthConstants.SESSION_COOKIE_NAME))
            .andExpect(cookie().exists(AuthConstants.CSRF_COOKIE_NAME))
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호 불일치")
    fun `should return 401 when password is incorrect`() {
        // given
        val jsonBody = objectMapper.writeValueAsString(LoginRequest(existingUser.email, "wrong-password"))

        // when & then
        mockMvc
            .perform(
                post("/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 유저")
    fun `should return 401 when user does not exist`() {
        // given
        val jsonBody = objectMapper.writeValueAsString(LoginRequest("invalid@email.com", "somepassword1234!"))

        // when & then
        mockMvc
            .perform(
                post("/v1/auth/login")
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
                get("/v1/auth/csrf-token"),
            ).andExpect(status().isNoContent)
            .andExpect(cookie().exists(AuthConstants.CSRF_COOKIE_NAME))
    }

    fun createTestUser(
        email: String,
        name: String,
        password: String,
    ): User {
        val user = User(email = email, name = name, password = password)
        user.hashPassword(passwordEncoder)
        return userRepository.createUser(user)
    }
}
