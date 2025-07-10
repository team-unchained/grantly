package grantly.app.adapter.`in`

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
import grantly.oauth.adapter.out.enums.OAuthClientScope
import grantly.oauth.adapter.out.enums.OAuthGrantType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
class AppClientControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val appRepository: AppRepository,
    @Autowired
    private val memberRepository: MemberRepository,
    @Autowired
    private val objectMapper: ObjectMapper,
    @Autowired
    private val appClientRepository: AppClientRepository,
) {
    @Test
    @DisplayName("OAuth 클라이언트 생성")
    @WithTestSessionMember
    fun createAppClient() {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity
        val app = createTestApp(true, requestMember.getId())
        val requestData =
            objectMapper.writeValueAsString(
                mapOf(
                    "title" to "Test OAuth Client",
                    "redirectUris" to listOf("https://example.com/callback"),
                    "scopes" to listOf(OAuthClientScope.ALL.value),
                    "grantType" to OAuthGrantType.AUTHORIZATION_CODE.value,
                ),
            )
        mockMvc
            .performWithSession(
                post("/v1/apps/${app.slug}/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("Test OAuth Client"))
            .andExpect(jsonPath("$.redirectUris").isArray)
            .andExpect(jsonPath("$.scopes").isArray)
    }

    @Test
    @DisplayName("OAuth 클라이언트 수정")
    @WithTestSessionMember
    fun updateAppClient() {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity
        val app = createTestApp(true, requestMember.getId())
        val appClient = createTestAppClient(app)
        val requestData =
            objectMapper.writeValueAsString(
                mapOf(
                    "title" to "Updated OAuth Client",
                    "redirectUris" to listOf("https://updated.com/callback"),
                    "scopes" to listOf(OAuthClientScope.ALL.value),
                    "grantType" to OAuthGrantType.AUTHORIZATION_CODE.value,
                ),
            )
        mockMvc
            .performWithSession(
                put("/v1/apps/${app.slug}/clients/${appClient.clientId}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated OAuth Client"))
            .andExpect(jsonPath("$.redirectUris[0]").value("https://updated.com/callback"))
            .andExpect(jsonPath("$.scopes").isArray)
            .andExpect(jsonPath("$.scopes.length()").value(1))
    }

    @Test
    @DisplayName("OAuth 클라이언트 삭제")
    @WithTestSessionMember
    fun deleteAppClient() {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity
        val app = createTestApp(true, requestMember.getId())
        val appClient = createTestAppClient(app)
        mockMvc
            .performWithSession(
                delete("/v1/apps/${app.slug}/clients/${appClient.clientId}"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isNoContent)
        // 실제 삭제 검증은 별도 repository에서 확인 필요
    }

    @Test
    @DisplayName("OAuth 클라이언트 단일 조회")
    @WithTestSessionMember
    fun getAppClient() {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity
        val app = createTestApp(true, requestMember.getId())
        val appClient = createTestAppClient(app)
        mockMvc
            .performWithSession(
                get("/v1/apps/${app.slug}/clients/${appClient.clientId}"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.clientId").value(appClient.clientId))
            .andExpect(jsonPath("$.title").value(appClient.title))
            .andExpect(jsonPath("$.redirectUris").isArray)
            .andExpect(jsonPath("$.scopes").isArray)
    }

    @Test
    @DisplayName("OAuth 클라이언트 목록 조회")
    @WithTestSessionMember
    fun getAppClients() {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity
        val app = createTestApp(true, requestMember.getId())
        createTestAppClient(app)
        createTestAppClient(app)
        createTestAppClient(app)
        mockMvc
            .performWithSession(
                get("/v1/apps/${app.slug}/clients"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].clientId").exists())
            .andExpect(jsonPath("$[0].title").exists())
    }

    @Test
    @DisplayName("앱의 소유자가 아닌 경우 OAuth 클라이언트 생성 불가")
    @WithTestSessionMember
    fun `should not allow non-owner to create app client`() {
        val ownerMember =
            memberRepository.createMember(
                MemberDomain(
                    email = "test2@email.com",
                    password = "pass1234!",
                    name = "Test User2",
                ),
            )
        val app = createTestApp(true, ownerMember.id)
        val requestData =
            objectMapper.writeValueAsString(
                mapOf(
                    "title" to "Test OAuth Client",
                    "redirectUris" to listOf("https://example.com/callback"),
                    "scopes" to listOf(OAuthClientScope.ALL.value),
                    "grantType" to OAuthGrantType.AUTHORIZATION_CODE.value,
                ),
            )
        mockMvc
            .performWithSession(
                post("/v1/apps/${app.slug}/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("앱의 소유자가 아닌 경우 OAuth 클라이언트 수정 불가")
    @WithTestSessionMember
    fun `should not allow non-owner to update app client`() {
        val ownerMember =
            memberRepository.createMember(
                MemberDomain(
                    email = "test2@email.com",
                    password = "pass1234!",
                    name = "Test User2",
                ),
            )
        val app = createTestApp(true, ownerMember.id)
        val appClient = createTestAppClient(app)
        val requestData =
            objectMapper.writeValueAsString(
                mapOf(
                    "title" to "Updated OAuth Client",
                    "redirectUris" to listOf("https://updated.com/callback"),
                    "scopes" to listOf(OAuthClientScope.ALL.value),
                    "grantType" to OAuthGrantType.AUTHORIZATION_CODE.value,
                ),
            )
        mockMvc
            .performWithSession(
                put("/v1/apps/${app.slug}/clients/${appClient.clientId}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("앱의 소유자가 아닌 경우 OAuth 클라이언트 삭제 불가")
    @WithTestSessionMember
    fun `should not allow non-owner to delete app client`() {
        val ownerMember =
            memberRepository.createMember(
                MemberDomain(
                    email = "test2@email.com",
                    password = "pass1234!",
                    name = "Test User2",
                ),
            )
        val app = createTestApp(true, ownerMember.id)
        val appClient = createTestAppClient(app)
        mockMvc
            .performWithSession(
                delete("/v1/apps/${app.slug}/clients/${appClient.clientId}"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("앱의 소유자가 아닌 경우 OAuth 클라이언트 조회 불가")
    @WithTestSessionMember
    fun `should not allow non-owner to get app client`() {
        val ownerMember =
            memberRepository.createMember(
                MemberDomain(
                    email = "test2@email.com",
                    password = "pass1234!",
                    name = "Test User2",
                ),
            )
        val app = createTestApp(true, ownerMember.id)
        val appClient = createTestAppClient(app)
        mockMvc
            .performWithSession(
                get("/v1/apps/${app.slug}/clients/${appClient.clientId}"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("앱의 소유자가 아닌 경우 OAuth 클라이언트 목록 조회 불가")
    @WithTestSessionMember
    fun `should not allow non-owner to get app clients`() {
        val ownerMember =
            memberRepository.createMember(
                MemberDomain(
                    email = "test2@email.com",
                    password = "pass1234!",
                    name = "Test User2",
                ),
            )
        val app = createTestApp(true, ownerMember.id)
        mockMvc
            .performWithSession(
                get("/v1/apps/${app.slug}/clients"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("존재하지 않는 OAuth 클라이언트 수정 시 404 에러")
    @WithTestSessionMember
    fun `should return 404 when updating non-existent app client`() {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity
        val app = createTestApp(true, requestMember.getId())
        val nonExistentClientId = "non-existent-client-id"

        val requestData =
            objectMapper.writeValueAsString(
                mapOf(
                    "title" to "Updated OAuth Client",
                    "redirectUris" to listOf("https://updated.com/callback"),
                    "scopes" to listOf(OAuthClientScope.ALL.value),
                    "grantType" to OAuthGrantType.AUTHORIZATION_CODE.value,
                ),
            )

        mockMvc
            .performWithSession(
                put("/v1/apps/${app.slug}/clients/$nonExistentClientId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("존재하지 않는 OAuth 클라이언트 삭제 시 404 에러")
    @WithTestSessionMember
    fun `should return 404 when deleting non-existent app client`() {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity
        val app = createTestApp(true, requestMember.getId())
        val nonExistentClientId = "non-existent-client-id"

        mockMvc
            .performWithSession(
                delete("/v1/apps/${app.slug}/clients/$nonExistentClientId"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("존재하지 않는 OAuth 클라이언트 조회 시 404 에러")
    @WithTestSessionMember
    fun `should return 404 when getting non-existent app client`() {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticationEntity
        val app = createTestApp(true, requestMember.getId())
        val nonExistentClientId = "non-existent-client-id"

        mockMvc
            .performWithSession(
                get("/v1/apps/${app.slug}/clients/$nonExistentClientId"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("존재하지 않는 앱에 OAuth 클라이언트 생성 시 403 에러")
    @WithTestSessionMember
    fun `should return 403 when creating app client for non-existent app`() {
        val nonExistentAppSlug = "non-existent-app"
        val requestData =
            objectMapper.writeValueAsString(
                mapOf(
                    "title" to "Test OAuth Client",
                    "redirectUris" to listOf("https://example.com/callback"),
                    "scopes" to listOf(OAuthClientScope.ALL.value),
                    "grantType" to OAuthGrantType.AUTHORIZATION_CODE.value,
                ),
            )

        mockMvc
            .performWithSession(
                post("/v1/apps/$nonExistentAppSlug/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isForbidden)
    }

    private fun createTestApp(
        isActive: Boolean = true,
        ownerId: Long,
    ): AppDomain =
        appRepository.createApp(
            AppDomain(
                name = "Test App",
                description = "Test Description",
                isActive = isActive,
                slug = UUID.randomUUID().toString(),
                ownerId = ownerId,
            ),
        )

    private fun createTestAppClient(app: AppDomain): AppClientDomain {
        val appClient =
            AppClientDomain(
                appId = app.id,
                title = "Test OAuth Client",
                clientId = UUID.randomUUID().toString(),
                clientSecret = "secret",
                redirectUris = mutableListOf("https://example.com/callback"),
                scopes = mutableListOf(OAuthClientScope.ALL),
                grantType = OAuthGrantType.AUTHORIZATION_CODE,
            )
        return appClientRepository.save(appClient)
    }
}
