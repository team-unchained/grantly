package grantly.common.exceptions

import org.springframework.http.HttpStatus

enum class HttpErrorType(
    val httpStatus: HttpStatus,
    val message: String,
) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    INSTANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "Not authorized for the resource"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "Authentication needed"),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Duplicate resource already exists"),
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable request"),
    CONFLICT(HttpStatus.CONFLICT, "Conflict"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, please try again"),
}
