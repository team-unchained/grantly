package grantly.member.adapter.`in`.dto.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [PasswordValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidPassword(
    val message: String =
        "Password must be at least 8 characters long " +
            "and contain at least one alphabet, one digit, and one special character" +
            "(@, $, !, %, *, ?, &, +, =, /, ^, #, -, _)",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
