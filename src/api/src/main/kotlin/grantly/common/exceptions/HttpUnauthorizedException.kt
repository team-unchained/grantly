package grantly.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class HttpUnauthorizedException : HttpException {
    constructor() : super(HttpErrorType.AUTHENTICATION_FAILED)
    constructor(message: String?) : super(HttpErrorType.AUTHENTICATION_FAILED, message)
    constructor(message: String, detail: Map<String, Any>) : super(HttpErrorType.AUTHENTICATION_FAILED, message, detail)
}
