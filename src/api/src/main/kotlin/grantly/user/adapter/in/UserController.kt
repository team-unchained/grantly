package grantly.user.adapter.`in`

import grantly.user.adapter.`in`.dto.SignUpRequest
import grantly.user.adapter.`in`.dto.UpdateUserRequest
import grantly.user.application.port.`in`.EditProfileUseCase
import grantly.user.application.port.`in`.FindUserQuery
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.application.port.`in`.dto.SignUpParams
import grantly.user.domain.User
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Tag(name = "사용자 API", description = "사용자 관련 API")
class UserController(
    private val signUpUseCase: SignUpUseCase,
    private val findUserQuery: FindUserQuery,
    private val editProfileUseCase: EditProfileUseCase,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun findAll(): List<User> {
        log.info("사용자 전체 목록을 조회합니다")
        val users = findUserQuery.findAllUsers()
        log.debug("조회된 사용자 수: {}", users.size)
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
