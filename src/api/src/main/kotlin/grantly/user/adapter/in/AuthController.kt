package grantly.user.adapter.`in`

import grantly.common.exceptions.ConflictException
import grantly.common.utils.HttpUtil
import grantly.common.utils.TimeUtil
import grantly.user.adapter.`in`.dto.SignUpRequest
import grantly.user.adapter.`in`.dto.UserResponse
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.application.service.exceptions.DuplicateEmailException
import grantly.user.domain.User
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@ResponseBody
@RequestMapping("/v1/auth")
class AuthController(
    private val signUpUseCase: SignUpUseCase,
) {
    @PostMapping("/signup")
    fun signUp(
        @Valid @RequestBody body: SignUpRequest,
    ): ResponseEntity<UserResponse> {
        val user: User
        try {
            user = signUpUseCase.signUp(SignUpParams(body.email, body.name, body.password))
        } catch (e: DuplicateEmailException) {
            throw ConflictException(e.message ?: "Email already exists")
        }
        return ResponseEntity.created(HttpUtil.buildLocationURI("/users/me")).body(
            UserResponse(
                id = user.id,
                email = user.email,
                name = user.name,
                createdAt = TimeUtil.toRFC3339(user.createdAt),
                modifiedAt = user.modifiedAt?.let { TimeUtil.toRFC3339(it) },
            ),
        )
    }
}
