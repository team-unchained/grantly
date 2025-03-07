package grantly.user.adapter.`in`.dto

import grantly.user.adapter.`in`.dto.validator.ValidPassword
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 요청 DTO")
data class SignUpRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email must not be blank")
    @field:Size(min = 1, max = 100, message = "Email must be between 1 and 100 characters")
    val email: String,
    @field:NotBlank(message = "Name must not be blank")
    @field:Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    val name: String,
    @field:NotBlank(message = "Password must not be blank")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:ValidPassword
    val password: String,
)
