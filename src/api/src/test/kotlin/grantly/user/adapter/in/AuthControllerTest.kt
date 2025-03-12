package grantly.user.adapter.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import grantly.user.adapter.`in`.dto.SignUpRequest
import grantly.user.application.port.out.UserRepository
import grantly.user.domain.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerTest(
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val objectMapper: ObjectMapper,
) {
    @Test
    @DisplayName("중복 이메일로 회원가입 불가")
    fun `should return 409 when email is already taken`() {
        // given
        val existingUser = User(email = "test@email.com", name = "test", password = "test123!")
        userRepository.createUser(existingUser)

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
}
