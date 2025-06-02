package grantly.member.adapter.`in`

import grantly.common.exceptions.HttpConflictException
import grantly.common.exceptions.HttpExceptionResponse
import grantly.common.exceptions.HttpUnauthorizedException
import grantly.common.exceptions.HttpUnprocessableException
import grantly.common.utils.HttpUtil
import grantly.member.adapter.`in`.dto.LoginRequest
import grantly.member.adapter.`in`.dto.PasswordResetRequest
import grantly.member.adapter.`in`.dto.SendEmailRequest
import grantly.member.adapter.`in`.dto.SignUpRequest
import grantly.member.adapter.out.dto.MemberResponse
import grantly.member.adapter.out.dto.SignUpResponse
import grantly.member.application.port.`in`.CsrfTokenUseCase
import grantly.member.application.port.`in`.LoginUseCase
import grantly.member.application.port.`in`.LogoutUseCase
import grantly.member.application.port.`in`.PasswordResetUseCase
import grantly.member.application.port.`in`.SignUpUseCase
import grantly.member.application.port.`in`.dto.LoginParams
import grantly.member.application.port.`in`.dto.LogoutParams
import grantly.member.application.port.`in`.dto.SignUpParams
import grantly.member.application.service.exceptions.DuplicateEmailException
import grantly.member.application.service.exceptions.PasswordMismatchException
import grantly.member.domain.Member
import grantly.token.application.service.exceptions.InvalidTokenException
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
    private val csrfTokenUseCase: CsrfTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val passwordResetUseCase: PasswordResetUseCase,
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
        val member: Member
        try {
            member = signUpUseCase.signUp(SignUpParams(body.email, body.name, body.password))
        } catch (e: DuplicateEmailException) {
            throw HttpConflictException(e.message ?: "Email already exists")
        }
        return ResponseEntity.created(HttpUtil.buildLocationURI("/v1/members/me")).body(
            SignUpResponse(
                member =
                    MemberResponse(
                        id = member.id,
                        email = member.email,
                        name = member.name,
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
            throw HttpUnauthorizedException("Authorization failed")
        } catch (e: PasswordMismatchException) {
            throw HttpUnauthorizedException(e.message)
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
        csrfTokenUseCase.issueCsrfToken(request, response)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "로그아웃",
        responses = [
            ApiResponse(responseCode = "204", description = "로그아웃 성공"),
        ],
    )
    @PostMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        logoutUseCase.logout(LogoutParams(request, response))
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "비밀번호 초기화 요청",
        responses = [
            ApiResponse(responseCode = "204", description = "비밀번호 초기화를 위한 이메일 전송 성공"),
        ],
    )
    @PostMapping("/request-password-reset")
    fun sendEmail(
        @Valid @RequestBody body: SendEmailRequest,
    ): ResponseEntity<Void> {
        passwordResetUseCase.requestPasswordReset(body.email)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "비밀번호 초기화",
        responses = [
            ApiResponse(responseCode = "204", description = "비밀번호 초기화 성공"),
            ApiResponse(responseCode = "422", description = "유효하지 않은 토큰"),
        ],
    )
    @PostMapping("/reset-password")
    fun resetPassword(
        @Valid @RequestBody body: PasswordResetRequest,
    ): ResponseEntity<Void> {
        try {
            passwordResetUseCase.resetPassword(body.token, body.password)
        } catch (e: InvalidTokenException) {
            throw HttpUnprocessableException("Invalid token")
        }
        return ResponseEntity.noContent().build()
    }
}
