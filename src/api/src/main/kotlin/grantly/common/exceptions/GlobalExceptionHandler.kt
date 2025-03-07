package grantly.common.exceptions

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(HttpException::class)
    fun handleCustomHttpException(
        exception: HttpException,
        request: WebRequest?,
    ): ResponseEntity<Any?> {
        var message: String? = exception.message
        if (message == null) {
            message = exception.errorType.message
        }
        val exceptionResponse =
            HttpExceptionResponse(
                exception.errorType.httpStatus.value(),
                message,
                exception.detail,
            )

        return ResponseEntity(exceptionResponse, exception.errorType.httpStatus)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage.orEmpty() }
        val httpErrorType = HttpErrorType.UNPROCESSABLE_ENTITY
        val exceptionResponse =
            HttpExceptionResponse(
                httpErrorType.httpStatus.value(),
                httpErrorType.message,
                errors,
            )
        return ResponseEntity(exceptionResponse, httpErrorType.httpStatus)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<Any?> {
        val errors = ex.constraintViolations.associate { it.propertyPath.toString() to it.message }
        val httpErrorType = HttpErrorType.UNPROCESSABLE_ENTITY
        val exceptionResponse =
            HttpExceptionResponse(
                httpErrorType.httpStatus.value(),
                httpErrorType.message,
                errors,
            )
        return ResponseEntity(exceptionResponse, httpErrorType.httpStatus)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val httpErrorType = HttpErrorType.BAD_REQUEST
        val exceptionResponse =
            HttpExceptionResponse(
                httpErrorType.httpStatus.value(),
                ex.message ?: httpErrorType.message,
            )
        return ResponseEntity(exceptionResponse, httpErrorType.httpStatus)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        println(ex.message)
        val exceptionResponse =
            HttpExceptionResponse(
                statusCode.value(),
                ex.message ?: "Internal Server Error",
            )
        return ResponseEntity(exceptionResponse, statusCode)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        exception: RuntimeException,
        request: WebRequest?,
    ): ResponseEntity<Any?> {
        val httpError = HttpErrorType.INTERNAL_SERVER_ERROR
        val exceptionResponse =
            HttpExceptionResponse(
                500,
                httpError.message,
            )
        // TODO: 실제로 발생한 Runtime exception 에 대한 로깅을 추가해야함
        return ResponseEntity(exceptionResponse, httpError.httpStatus)
    }
}
