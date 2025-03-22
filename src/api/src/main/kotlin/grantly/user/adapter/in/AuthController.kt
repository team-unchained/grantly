package grantly.user.adapter.`in`

import grantly.common.exceptions.HttpConflictException
import grantly.common.exceptions.HttpExceptionResponse
import grantly.common.exceptions.HttpInternalServerErrorException
import grantly.common.exceptions.HttpUnauthorizedException
import grantly.common.utils.HttpUtil
import grantly.common.utils.TimeUtil
import grantly.user.adapter.`in`.dto.LoginRequest
import grantly.user.adapter.`in`.dto.SignUpRequest
import grantly.user.adapter.out.dto.SignUpResponse
import grantly.user.adapter.out.dto.UserResponse
import grantly.user.application.port.`in`.LoginUseCase
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.LoginParams
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.application.service.exceptions.DuplicateEmailException
import grantly.user.application.service.exceptions.PasswordMismatchException
import grantly.user.application.service.exceptions.TokenGenerationException
import grantly.user.domain.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@ResponseBody
@RequestMapping("/v1/auth")
@Tag(name = "인증", description = "인증 관련 API")
class AuthController(
    private val signUpUseCase: SignUpUseCase,
    private val loginUseCase: LoginUseCase,
) {
    @Operation(
        summary = "이메일을 이용한 회원가입",
        responses = [
            ApiResponse(responseCode = "201", description = "회원가입 성공"),
            ApiResponse(
                responseCode = "409",
                description = "이미 존재하는 이메일로 회원가입을 시도할 경우",
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
                description = "요청 본문의 유효성 검사에 실패하는 경우",
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
    @PostMapping("/signup")
    fun signUp(
        @Valid @RequestBody body: SignUpRequest,
    ): ResponseEntity<SignUpResponse> {
        val user: User
        try {
            user = signUpUseCase.signUp(SignUpParams(body.email, body.name, body.password))
        } catch (e: DuplicateEmailException) {
            throw HttpConflictException(e.message ?: "Email already exists")
        }
        // TODO: 토큰 발급 및 응답에 포함
        return ResponseEntity.created(HttpUtil.buildLocationURI("/v1/users/me")).body(
            SignUpResponse(
                user =
                    UserResponse(
                        id = user.id,
                        email = user.email,
                        name = user.name,
                        createdAt = TimeUtil.toRFC3339(user.createdAt),
                        modifiedAt = user.modifiedAt?.let { TimeUtil.toRFC3339(it) },
                    ),
            ),
        )
    }

    @Operation(
        summary = "이메일을 이용한 로그인",
        responses = [
            ApiResponse(responseCode = "204", description = "로그인 성공. session_token 키로 쿠키 설정"),
            ApiResponse(
                responseCode = "401",
                description = "비밀번호 불일치, 존재하지 않는 유저",
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
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody body: LoginRequest,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        try {
            loginUseCase.login(LoginParams(body.email, body.password, request, response))
        } catch (e: EntityNotFoundException) {
            throw HttpUnauthorizedException(e.message)
        } catch (e: PasswordMismatchException) {
            throw HttpUnauthorizedException(e.message)
        } catch (e: TokenGenerationException) {
            throw HttpInternalServerErrorException(e.message)
        }
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "CSRF 토큰 조회",
        responses = [
            ApiResponse(responseCode = "204", description = "CSRF 토큰 조회 성공. CSRF-TOKEN 키로 쿠키 설정"),
        ],
    )
    @GetMapping("/csrf-token")
    fun getCsrfToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        loginUseCase.setCsrfToken(request, response)
        return ResponseEntity.noContent().build()
    }
}
