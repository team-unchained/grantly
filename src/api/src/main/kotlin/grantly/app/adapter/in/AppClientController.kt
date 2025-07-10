package grantly.app.adapter.`in`

import grantly.app.adapter.`in`.dto.AppClientResponse
import grantly.app.adapter.`in`.dto.CreateAppClientRequest
import grantly.app.adapter.`in`.dto.UpdateAppClientRequest
import grantly.app.application.port.`in`.CreateAppClientUseCase
import grantly.app.application.port.`in`.DeleteAppClientUseCase
import grantly.app.application.port.`in`.FindAppClientQuery
import grantly.app.application.port.`in`.UpdateAppClientUseCase
import grantly.app.application.port.`in`.dto.CreateAppClientParams
import grantly.app.application.port.`in`.dto.DeleteAppClientParams
import grantly.app.application.port.`in`.dto.FindAppClientParams
import grantly.app.application.port.`in`.dto.UpdateAppClientParams
import grantly.common.exceptions.HttpNotFoundException
import grantly.config.AuthenticationEntity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@ResponseBody
@RequestMapping("/admin/v1/apps/{appSlug}/clients")
@Tag(name = "OAuth 클라이언트", description = "멤버가 등록한 앱의 OAuth 클라이언트 관련 API")
class AppClientController(
    private val createAppClientUseCase: CreateAppClientUseCase,
    private val updateAppClientUseCase: UpdateAppClientUseCase,
    private val deleteAppClientUseCase: DeleteAppClientUseCase,
    private val findAppClientQuery: FindAppClientQuery,
    private val appGuard: AppGuard,
) {
    @Operation(
        summary = "새로운 OAuth 클라이언트 생성",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "OAuth 클라이언트 생성 성공",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = AppClientResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @PostMapping
    fun createAppClient(
        @PathVariable appSlug: String,
        @AuthenticationPrincipal requestMember: AuthenticationEntity,
        @Valid @RequestBody request: CreateAppClientRequest,
    ): ResponseEntity<AppClientResponse> {
        val app = appGuard.checkAccess(appSlug, requestMember.getId())

        val params =
            CreateAppClientParams(
                appId = app.id,
                title = request.title,
                redirectUris = request.redirectUris,
                scopes = request.scopes,
                grantType = request.grantType,
            )

        val appClient = createAppClientUseCase.createAppClient(params)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AppClientResponse.from(appClient))
    }

    @Operation(
        summary = "OAuth 클라이언트 수정",
        responses = [
            ApiResponse(
                responseCode = "200",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = AppClientResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @PutMapping("/{clientId}")
    fun updateAppClient(
        @PathVariable appSlug: String,
        @PathVariable clientId: String,
        @AuthenticationPrincipal requestMember: AuthenticationEntity,
        @Valid @RequestBody request: UpdateAppClientRequest,
    ): ResponseEntity<AppClientResponse> {
        val app = appGuard.checkAccess(appSlug, requestMember.getId())

        val params =
            UpdateAppClientParams(
                appId = app.id,
                clientId = clientId,
                title = request.title,
                redirectUris = request.redirectUris,
                scopes = request.scopes,
                grantType = request.grantType,
            )

        val appClient =
            try {
                updateAppClientUseCase.updateAppClient(params)
            } catch (_: EntityNotFoundException) {
                throw HttpNotFoundException("OAuth client not found.")
            }

        return ResponseEntity.ok(AppClientResponse.from(appClient))
    }

    @Operation(
        summary = "OAuth 클라이언트 삭제",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "OAuth 클라이언트 삭제 성공",
            ),
        ],
    )
    @DeleteMapping("/{clientId}")
    fun deleteAppClient(
        @PathVariable appSlug: String,
        @PathVariable clientId: String,
        @AuthenticationPrincipal requestMember: AuthenticationEntity,
    ): ResponseEntity<Unit> {
        val app = appGuard.checkAccess(appSlug, requestMember.getId())

        val params =
            DeleteAppClientParams(
                appId = app.id,
                clientId = clientId,
            )

        try {
            deleteAppClientUseCase.deleteAppClient(params)
        } catch (_: EntityNotFoundException) {
            throw HttpNotFoundException("OAuth client not found.")
        }

        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "OAuth 클라이언트 단일 조회",
        responses = [
            ApiResponse(
                responseCode = "200",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = AppClientResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @GetMapping("/{clientId}")
    fun getAppClient(
        @PathVariable appSlug: String,
        @PathVariable clientId: String,
        @AuthenticationPrincipal requestMember: AuthenticationEntity,
    ): ResponseEntity<AppClientResponse> {
        val app = appGuard.checkAccess(appSlug, requestMember.getId())

        val params =
            FindAppClientParams(
                appId = app.id,
                clientId = clientId,
            )

        val appClient =
            try {
                findAppClientQuery.findAppClient(params)
            } catch (_: EntityNotFoundException) {
                throw HttpNotFoundException("OAuth client not found.")
            }

        return ResponseEntity.ok(AppClientResponse.from(appClient))
    }

    @Operation(
        summary = "OAuth 클라이언트 목록 조회",
        responses = [
            ApiResponse(
                responseCode = "200",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = AppClientResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @GetMapping
    fun getAppClients(
        @PathVariable appSlug: String,
        @AuthenticationPrincipal requestMember: AuthenticationEntity,
    ): ResponseEntity<List<AppClientResponse>> {
        val app = appGuard.checkAccess(appSlug, requestMember.getId())

        val appClients = findAppClientQuery.findAppClientsByAppId(app.id)
        val responses = appClients.map { AppClientResponse.from(it) }
        return ResponseEntity.ok(responses)
    }
}
