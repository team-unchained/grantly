package grantly.oauth.adapter.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import grantly.app.application.port.out.AppClientRepository
import grantly.app.application.port.out.AppRepository
import grantly.app.domain.AppClientDomain
import grantly.app.domain.AppDomain
import grantly.common.utils.performWithSession
import grantly.config.AuthenticationEntity
import grantly.config.TestSessionTokenHolder
import grantly.config.WithTestSessionMember
import grantly.member.application.port.out.MemberRepository
import grantly.member.domain.MemberDomain
import grantly.oauth.adapter.`in`.dto.AuthorizeClientRequest
import grantly.oauth.application.port.out.AuthorizationCodeRepository
import grantly.oauth.domain.enums.OAuthClientScope
import grantly.oauth.domain.enums.OAuthGrantType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@WithTestSessionMember(email = "test@email.com", name = "testUser")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class OAuthControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val memberRepository: MemberRepository,
    private val appRepository: AppRepository,
    private val appClientRepository: AppClientRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository
) {
    @AfterEach
    fun tearDown() = TestSessionTokenHolder.clear()

    @Test
    @DisplayName("특정 앱 클라이언트에 대해 권한 부여")
    fun `authorize client`() {
        // given
        // make test user
        val testUser =
            memberRepository.createMember(MemberDomain(email = "dev@email.com", name = "dev", password = "devPassword"))
        // make test app & client
        val testApp = appRepository.createApp(AppDomain(name = "Test App", slug = "test-app", ownerId = testUser.id))
        val testClient = AppClientDomain(
            appId = testApp.id,
            clientId = "test-client-id",
            clientSecret = "test-client-secret",
            title = "Test Client",
            redirectUris = mutableListOf("https://example.com/callback"),
            scopes = mutableListOf(OAuthClientScope.ALL),
            grantType = OAuthGrantType.AUTHORIZATION_CODE
        )
        appClientRepository.createAppClient(testClient)
        val requestUser = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity

        // when and then
        val jsonBody = objectMapper.writeValueAsString(
            AuthorizeClientRequest(
                clientId = testClient.clientId, scopes = listOf("all"),
                redirectUri = "https://example.com/callback", responseType = "code", state = "testState"
            )
        )
        mockMvc.performWithSession(
            post("/oauth/v1/authorize")
                .content(jsonBody),
            TestSessionTokenHolder.get()
        )

        // consent 생겼는지 확인
        assertDoesNotThrow {
            appClientRepository.findByAppIdAndClientId(testApp.id, testClient.clientId)
        }
        // redis에 authorization code가 저장되었는지 확인
        assertNotNull(authorizationCodeRepository.getAndRemove(testClient.clientId, requestUser.getId()))
    }
}
