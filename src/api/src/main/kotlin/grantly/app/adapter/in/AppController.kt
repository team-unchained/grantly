package grantly.app.adapter.`in`

import grantly.app.adapter.`in`.dto.CreateAppRequest
import grantly.app.adapter.`in`.dto.UpdateAppRequest
import grantly.app.adapter.out.dto.AppResponse
import grantly.app.adapter.out.dto.CreateAppResponse
import grantly.app.adapter.out.dto.SimpleAppResponse
import grantly.app.application.port.`in`.CreateAppUseCase
import grantly.app.application.port.`in`.DeleteAppImageUseCase
import grantly.app.application.port.`in`.DeleteAppUseCase
import grantly.app.application.port.`in`.FindAppQuery
import grantly.app.application.port.`in`.UpdateAppUseCase
import grantly.app.application.port.`in`.UploadAppImageUseCase
import grantly.app.application.port.`in`.dto.CreateAppParams
import grantly.app.application.port.`in`.dto.UpdateAppParams
import grantly.app.application.service.exceptions.CannotDeleteLastActiveAppException
import grantly.common.core.store.FileSystemStorage
import grantly.common.core.store.exceptions.ResourceTooLargeException
import grantly.common.core.store.exceptions.UnprocessableResourceException
import grantly.common.exceptions.HttpBadRequestException
import grantly.common.exceptions.HttpContentTooLargeException
import grantly.common.exceptions.HttpExceptionResponse
import grantly.common.exceptions.HttpForbiddenException
import grantly.common.exceptions.HttpNotFoundException
import grantly.common.exceptions.HttpUnprocessableException
import grantly.common.exceptions.PermissionDeniedException
import grantly.common.utils.HttpUtil
import grantly.config.AuthenticatedMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@ResponseBody
@RequestMapping("/admin/v1/apps")
@Tag(name = "앱", description = "멤버가 등록한 앱 관련 API")
class AppController(
    private val findAppQuery: FindAppQuery,
    private val updateAppUseCase: UpdateAppUseCase,
    private val deleteAppUseCase: DeleteAppUseCase,
    private val createAppUseCase: CreateAppUseCase,
    private val uploadAppImageUseCase: UploadAppImageUseCase,
    private val deleteAppImageUseCase: DeleteAppImageUseCase,
    private val appGuard: AppGuard,
    @Value("\${grantly.service.api-domain}")
    private val serverDomain: String,
    private val fileSystemStorage: FileSystemStorage,
) {
    @Operation(
        summary = "요청자가 owner인 앱 목록 조회",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "앱 목록 조회 성공",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = SimpleAppResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @GetMapping("")
    fun getApps(
        @AuthenticationPrincipal requestMember: AuthenticatedMember,
    ): ResponseEntity<List<SimpleAppResponse>> {
        val apps = findAppQuery.findAppsByOwnerId(requestMember.getId())
        return ResponseEntity.ok(
            apps.map {
                SimpleAppResponse(
                    id = it.id,
                    slug = it.slug,
                    name = it.name,
                    imageUrl = it.resolveFullImageUrl(serverDomain, fileSystemStorage.getStorageName()),
                    ownerId = it.ownerId,
                )
            },
        )
    }

    @Operation(
        summary = "새로운 앱 생성",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "앱 생성 성공",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = AppResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @PostMapping("")
    fun createApp(
        @RequestBody body: CreateAppRequest,
        @AuthenticationPrincipal requestMember: AuthenticatedMember,
    ): ResponseEntity<CreateAppResponse> {
        val newApp =
            createAppUseCase.createApp(
                CreateAppParams(
                    name = body.name,
                    description = body.description,
                    ownerId = requestMember.getId(),
                ),
            )

        return ResponseEntity.created(HttpUtil.buildLocationURI("/v1/apps/${newApp.id}")).body(
            CreateAppResponse(
                app =
                    AppResponse(
                        id = newApp.id,
                        slug = newApp.slug,
                        name = newApp.name,
                        description = newApp.description,
                        imageUrl = newApp.resolveFullImageUrl(serverDomain, fileSystemStorage.getStorageName()),
                        ownerId = newApp.ownerId,
                        createdAt = newApp.createdAt,
                        modifiedAt = newApp.modifiedAt ?: newApp.createdAt,
                    ),
            ),
        )
    }

    @Operation(
        summary = "앱 이미지 업로드",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "이미지 업로드 성공",
            ),
            ApiResponse(
                responseCode = "400",
                description = "이미지 형식이 잘못된 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "403",
                description = "앱의 소유자가 아닌 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "404",
                description = "앱이 존재하지 않는 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "413",
                description = "이미지 용량 제한 2MB 초과",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = AppResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @PostMapping("{appSlug}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @PathVariable appSlug: String,
        @RequestPart(required = true) image: MultipartFile,
        @AuthenticationPrincipal requestMember: AuthenticatedMember,
    ): ResponseEntity<Void> {
        try {
            uploadAppImageUseCase.uploadImage(appSlug, requestMember.getId(), image)
        } catch (e: ResourceTooLargeException) {
            throw HttpContentTooLargeException()
        } catch (e: UnprocessableResourceException) {
            throw HttpBadRequestException(e.message)
        } catch (e: EntityNotFoundException) {
            throw HttpNotFoundException("App not found.")
        } catch (e: PermissionDeniedException) {
            throw HttpForbiddenException()
        }

        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "앱 이미지 초기화",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "이미지 초기화 성공",
            ),
            ApiResponse(
                responseCode = "403",
                description = "앱의 소유자가 아닌 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "404",
                description = "앱이 존재하지 않는 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @DeleteMapping("{appSlug}/image")
    fun deleteImage(
        @PathVariable appSlug: String,
        @AuthenticationPrincipal requestMember: AuthenticatedMember,
    ): ResponseEntity<Void> {
        try {
            deleteAppImageUseCase.deleteImage(appSlug, requestMember.getId())
        } catch (e: EntityNotFoundException) {
            throw HttpNotFoundException("App not found.")
        } catch (e: PermissionDeniedException) {
            throw HttpForbiddenException()
        }

        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "앱 삭제",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "soft delete 수행",
            ),
            ApiResponse(
                responseCode = "403",
                description = "앱의 소유자가 아닌 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "404",
                description = "앱이 존재하지 않는 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "422",
                description = "활성화 상태의 앱이 1개 이하라 삭제가 제한된 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @DeleteMapping("/{slug}")
    fun deleteApp(
        @PathVariable slug: String,
        @AuthenticationPrincipal requestMember: AuthenticatedMember,
    ): ResponseEntity<Void> {
        try {
            deleteAppUseCase.deleteApp(slug, requestMember.getId())
        } catch (_: EntityNotFoundException) {
            throw HttpNotFoundException("App not found.")
        } catch (_: PermissionDeniedException) {
            throw HttpForbiddenException()
        } catch (e: CannotDeleteLastActiveAppException) {
            throw HttpUnprocessableException(e.message)
        }
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "앱 단일 조회",
        responses = [
            ApiResponse(
                responseCode = "200",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = AppResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "403",
                description = "앱의 소유자가 아닌 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "404",
                description = "앱이 존재하지 않거나 삭제된 앱인 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @GetMapping("/{slug}")
    fun getApp(
        @PathVariable slug: String,
        @AuthenticationPrincipal requestMember: AuthenticatedMember,
    ): ResponseEntity<AppResponse> {
        val app = appGuard.checkAccess(slug, requestMember.getId())

        return ResponseEntity.ok(
            AppResponse(
                id = app.id,
                slug = app.slug,
                name = app.name,
                description = app.description,
                imageUrl =
                    app.imageUrl?.let {
                        app.resolveFullImageUrl(
                            serverDomain,
                            fileSystemStorage.getStorageName(),
                        )
                    },
                ownerId = app.ownerId,
                createdAt = app.createdAt,
                modifiedAt = app.modifiedAt ?: app.createdAt,
            ),
        )
    }

    @Operation(
        summary = "앱 수정",
        responses = [
            ApiResponse(
                responseCode = "200",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = AppResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "403",
                description = "앱의 소유자가 아닌 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
            ApiResponse(
                responseCode = "404",
                description = "앱이 존재하지 않거나 삭제된 앱인 경우",
                content =
                    arrayOf(
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = HttpExceptionResponse::class),
                        ),
                    ),
            ),
        ],
    )
    @PutMapping("/{slug}")
    fun updateApp(
        @PathVariable slug: String,
        @RequestBody body: UpdateAppRequest,
        @AuthenticationPrincipal requestMember: AuthenticatedMember,
    ): ResponseEntity<AppResponse> {
        val app =
            try {
                updateAppUseCase.updateApp(
                    UpdateAppParams(
                        slug = slug,
                        name = body.name,
                        description = body.description,
                        ownerId = requestMember.getId(),
                    ),
                )
            } catch (_: EntityNotFoundException) {
                throw HttpNotFoundException("App not found.")
            } catch (_: PermissionDeniedException) {
                throw HttpForbiddenException()
            }

        return ResponseEntity.ok(
            AppResponse(
                id = app.id,
                slug = app.slug,
                name = app.name,
                description = app.description,
                imageUrl = app.resolveFullImageUrl(serverDomain, fileSystemStorage.getStorageName()),
                ownerId = app.ownerId,
                createdAt = app.createdAt,
                modifiedAt = app.modifiedAt ?: app.createdAt,
            ),
        )
    }
}
