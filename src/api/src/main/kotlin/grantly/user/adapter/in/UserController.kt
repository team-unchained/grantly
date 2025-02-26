package grantly.user.adapter.`in`

import grantly.user.adapter.`in`.dto.SignUpRequest
import grantly.user.adapter.`in`.dto.UpdateUserRequest
import grantly.user.application.port.`in`.EditProfileUseCase
import grantly.user.application.port.`in`.FindUserQuery
import grantly.user.application.port.`in`.SignUpUseCase
import grantly.user.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val signUpUseCase: SignUpUseCase,
    private val findUserQuery: FindUserQuery,
    private val editProfileUseCase: EditProfileUseCase,
) {
    @GetMapping
    fun findAll(): List<User> = findUserQuery.findAllUsers()

    @GetMapping("/{userId}")
    fun findById(
        @PathVariable userId: Long,
    ): User = findUserQuery.findUserById(userId)

    @PostMapping
    fun signUp(
        @RequestBody body: SignUpRequest,
    ): User {
        val newUser = signUpUseCase.signUp(body.email, body.password, body.name)
        return newUser
    }

    @PatchMapping("/{userId}")
    fun update(
        @PathVariable userId: Long,
        @RequestBody body: UpdateUserRequest,
    ): User {
        val newUser = editProfileUseCase.update(userId, body.name)
        return newUser
    }
}
