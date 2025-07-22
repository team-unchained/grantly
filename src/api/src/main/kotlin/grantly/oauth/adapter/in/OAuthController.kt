package grantly.oauth.adapter.`in`

import grantly.common.exceptions.HttpBadRequestException
import grantly.common.exceptions.HttpExceptionResponse
import grantly.config.AuthenticationEntity
import grantly.oauth.adapter.`in`.dto.AuthorizeClientRequest
import grantly.oauth.application.port.`in`.AuthorizationCodeUseCase
import grantly.oauth.application.port.`in`.dto.AuthorizeClientParams
import grantly.oauth.domain.enums.OAuthResponseType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import okhttp3.internal.http.HTTP_SEE_OTHER
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth/v1")
@Tag(name = "OAuth API", description = "OAuth 인증 관련 API")
class OAuthController(
    private val authorizationCodeUseCase: AuthorizationCodeUseCase,
) {
    @Operation(
        summary = "클라이언트 권한 부여",
        description = "현재까지 지원하는 grant_type은 authorization_code입니다",
        responses = [
            ApiResponse(
                responseCode = "303",
                description = "권한 부여 성공, 리다이렉트 URI로 이동",
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청, 지원하지 않는 response_type",
                content = [Content(schema = Schema(implementation = HttpExceptionResponse::class))],
            )
        ],
    )
    @PostMapping("/authorize")
    fun authorizeClient(
        @Valid @RequestBody body: AuthorizeClientRequest,
        @AuthenticationPrincipal requestUser: AuthenticationEntity,
    ): ResponseEntity<Void> {
        when (body.responseType) {
            OAuthResponseType.CODE.value -> {
                val redirectUri = authorizationCodeUseCase.authorizeClient(
                    AuthorizeClientParams(
                        clientId = body.clientId,
                        scopes = body.scopes,
                        redirectUri = body.redirectUri,
                        state = body.state,
                        responseType = body.responseType,
                        userId = requestUser.getId(),
                    )
                )
                return ResponseEntity.status(HTTP_SEE_OTHER).header("Location", redirectUri).build<Void>()
            }

            else -> {
                throw HttpBadRequestException("Unsupported response type: ${body.responseType}")
            }
        }
    }
}
