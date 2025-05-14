package grantly.user.adapter.`in`

import grantly.common.utils.performWithSession
import grantly.config.TestSessionTokenHolder
import grantly.config.WithTestSessionUser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
) {
    @AfterEach
    fun tearDown() = TestSessionTokenHolder.clear()

    @Test
    @DisplayName("요청 사용자 정보 조회")
    @WithTestSessionUser(email = "test@email.com", name = "test")
    fun `should get request user data`() {
        // given
        // when & then
        mockMvc
            .performWithSession(
                get("/v1/users/me"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@email.com"))
            .andExpect(jsonPath("$.name").value("test"))
    }
}
