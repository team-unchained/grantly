package grantly.app.adapter.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import grantly.app.application.port.out.AppRepository
import grantly.app.domain.AppDomain
import grantly.common.core.store.FileSystemStorage
import grantly.common.core.store.exceptions.NoSuchResourceException
import grantly.common.utils.performWithSession
import grantly.config.AuthenticatedMember
import grantly.config.TestSessionTokenHolder
import grantly.config.WithTestSessionMember
import grantly.member.application.port.out.MemberRepository
import grantly.member.domain.MemberDomain
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.OffsetDateTime
import java.util.UUID

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val appRepository: AppRepository,
    @Autowired
    private val memberRepository: MemberRepository,
    @Autowired
    private val objectMapper: ObjectMapper,
    @Autowired
    private val fileSystemStorage: FileSystemStorage,
) {
    @Test
    @DisplayName("활성화된 애플리케이션만 목록에 포함하여 조회")
    @WithTestSessionMember
    fun `should exclude deactivated apps on list action`() {
        // given
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticatedMember
        createTestApp(true, requestMember.getId())
        createTestApp(false, requestMember.getId())
        val latestApp = createTestApp(true, requestMember.getId())

        // when & then
        mockMvc
            .performWithSession(
                get("/v1/apps"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(latestApp.id))
    }

    @Test
    @DisplayName("앱 생성")
    @WithTestSessionMember
    fun createApp() {
        // given
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticatedMember
        val requestData =
            objectMapper.writeValueAsString(
                mapOf(
                    "name" to "Test App",
                    "description" to "Test Description",
                ),
            )
        // when & then
        mockMvc
            .performWithSession(
                post("/v1/apps")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isCreated)
            .andExpect { result ->
                val response = objectMapper.readTree(result.response.contentAsString)
                val appNode = response.get("app")
                val createdAppId = appNode.get("id").asLong()
                assertDoesNotThrow { appRepository.getAppById(createdAppId) }
                val createdAppOwnerId = appNode.get("ownerId").asLong()
                assert(requestMember.getId() == createdAppOwnerId)
            }
    }

    @Test
    @DisplayName("앱 삭제")
    @WithTestSessionMember
    fun deleteApp() {
        // given
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticatedMember
        val app = createTestApp(true, requestMember.getId())
        createTestApp(true, requestMember.getId())

        // when & then
        mockMvc
            .performWithSession(
                delete("/v1/apps/${app.slug}"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isNoContent)
            .andExpect {
                assertThrows<EntityNotFoundException> { appRepository.getAppById(app.id) }
            }
    }

    @Test
    @DisplayName("앱의 소유자만 삭제 가능")
    @WithTestSessionMember
    fun `should only allow app owner to delete`() {
        // given
        val newMember =
            memberRepository.createMember(
                MemberDomain(
                    email = "test2@email.com",
                    password = "pass1234!",
                    name = "Test User2",
                ),
            )
        val app = createTestApp(true, newMember.id) // 다른 소유자가 만든 앱

        // when & then
        mockMvc
            .performWithSession(
                delete("/v1/apps/${app.slug}"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("활성화 상태의 앱이 1개뿐일 때 삭제 불가")
    @WithTestSessionMember
    fun `cannot delete the only active app`() {
        // given
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticatedMember
        val app = createTestApp(true, requestMember.getId())

        // when & then
        mockMvc
            .performWithSession(
                delete("/v1/apps/${app.slug}"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isUnprocessableEntity)
    }

    @Test
    @DisplayName("앱 메타 데이터 수정")
    @WithTestSessionMember
    fun updateApp() {
        // given
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticatedMember
        val app = createTestApp(true, requestMember.getId())

        // when & then
        mockMvc
            .performWithSession(
                put("/v1/apps/${app.slug}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            mapOf(
                                "name" to "Updated App",
                                "description" to "Updated Description",
                            ),
                        ),
                    ),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isOk)
            .andExpect { result ->
                val response = objectMapper.readTree(result.response.contentAsString)
                assert(response.get("name").asText() == "Updated App")
                assert(OffsetDateTime.parse(response.get("modifiedAt").asText()) > app.modifiedAt)
            }
    }

    @Test
    @DisplayName("이미지 업로드 시 이미지 경로가 올바르게 저장된다.")
    @WithTestSessionMember
    fun uploadApplicationImage() {
        // given
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticatedMember
        val app = createTestApp(true, requestMember.getId())

        val mockFile =
            MockMultipartFile(
                "image", // 요청 파라미터 이름
                "someImage.png", // 파일 이름
                "image/png", // MIME 타입
                "dummy image content".toByteArray(), // 바이트 배열로 된 파일 내용
            )

        // when & then
        mockMvc
            .performWithSession(
                multipart("/v1/apps/${app.slug}/image")
                    .file(mockFile),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isNoContent)
            .andExpect {
                val app = appRepository.getAppById(app.id)
                assert(app.imageUrl == "/applications/images/${app.slug}.png")
            }
    }

    @Test
    @DisplayName("이미지 초기화 시 이미지 경로가 null 로 저장되고 파일이 제거된다.")
    @WithTestSessionMember
    fun deleteApplicationImage() {
        // given
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticatedMember
        val app = createTestApp(true, requestMember.getId())
        // 이미지 저장
        runBlocking {
            fileSystemStorage.put(
                "applications/images/${app.slug}.png",
                "dummy image content".toByteArray(),
                overwrite = true,
            )
        }
        app.imageUrl = "/applications/images/${app.slug}.png"
        appRepository.updateApp(app)

        // when & then
        mockMvc
            .performWithSession(
                delete("/v1/apps/${app.slug}/image"),
                TestSessionTokenHolder.get(),
            ).andExpect(status().isNoContent)
            .andExpect {
                val updatedApp = appRepository.getAppById(app.id)
                assert(updatedApp.imageUrl == null)
                assertThrows<NoSuchResourceException> {
                    runBlocking { fileSystemStorage.get("/applications/images/${app.slug}.png") }
                }
            }
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
}
