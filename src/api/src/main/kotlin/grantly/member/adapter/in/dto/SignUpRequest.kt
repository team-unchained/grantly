package grantly.member.adapter.`in`.dto

import grantly.member.adapter.`in`.dto.validator.ValidPassword
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

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
    @field:Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    @field:ValidPassword
    @Schema(description = "최소 8자리. 알파벳, 숫자, 특수문자(@, $, !, %, *, ?, &, +, =, /, ^, #, -, _) 하나 이상 포함")
    val password: String,
)
