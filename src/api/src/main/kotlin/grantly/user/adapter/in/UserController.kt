package grantly.user.adapter.`in`

import grantly.common.exceptions.HttpNotFoundException
import grantly.config.AuthenticatedUser
import grantly.user.adapter.`in`.dto.SignUpRequest
import grantly.user.adapter.`in`.dto.UpdateUserRequest
import grantly.user.adapter.out.dto.UserResponse
import grantly.user.application.port.`in`.EditProfileUseCase
import grantly.user.application.port.`in`.FindUserQuery
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.domain.User
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/v1/users")
@Tag(name = "사용자 API", description = "사용자 관련 API")
class UserController(
    private val signUpUseCase: SignUpUseCase,
    private val findUserQuery: FindUserQuery,
    private val editProfileUseCase: EditProfileUseCase,
) {
    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val requestUser = SecurityContextHolder.getContext().authentication.principal as AuthenticatedUser
        val user: User
        try {
            user = findUserQuery.findUserById(requestUser.getId())
        } catch (e: EntityNotFoundException) {
            throw HttpNotFoundException("User not found")
        }
        return ResponseEntity.ok(
            UserResponse(
                id = user.id,
                email = user.email,
                name = user.name,
            ),
        )
    }

    @GetMapping
    fun findAll(): List<User> {
        log.info { "사용자 전체 목록을 조회합니다" }
        val users = findUserQuery.findAllUsers()
        log.debug { "조회된 사용자 수: ${users.size}" }
        return users
    }

    @GetMapping("/{userId}")
    fun findById(
        @PathVariable userId: Long,
    ): User = findUserQuery.findUserById(userId)

    @PostMapping
    fun signUp(
        @RequestBody body: SignUpRequest,
    ): User = signUpUseCase.signUp(SignUpParams(body.email, body.name, body.password))

    @PatchMapping("/{userId}")
    fun update(
        @PathVariable userId: Long,
        @RequestBody body: UpdateUserRequest,
    ): User = editProfileUseCase.update(userId, body.name)
}
