package grantly.member.adapter.`in`.dto

import grantly.member.adapter.`in`.dto.validator.ValidPassword
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "비밀번호 재설정 요청 DTO")
data class PasswordResetRequest(
    @field:NotBlank(message = "token must not be blank")
    val token: String,
    @field:ValidPassword
    @Schema(description = "최소 8자리. 알파벳, 숫자, 특수문자(@, $, !, %, *, ?, &, +, =, /, ^, #, -, _) 하나 이상 포함")
    val password: String,
)
