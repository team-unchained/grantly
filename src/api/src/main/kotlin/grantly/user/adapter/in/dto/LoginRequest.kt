package grantly.user.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "로그인 요청 DTO")
data class LoginRequest(
    @field:Email(message = "Invalid email format")
    val email: String,
    @field:NotBlank(message = "Password must not be blank")
    val password: String,
)
