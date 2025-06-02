package grantly.member.adapter.`in`.dto.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordValidator : ConstraintValidator<ValidPassword, String> {
    override fun isValid(
        password: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (password == null) {
            return false
        }
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&+=/^#_-])[A-Za-z\\d@\$!%*?&+=/^#]{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }
}
