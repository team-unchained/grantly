package grantly.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class HttpForbiddenException : HttpException {
    constructor() : super(HttpErrorType.NO_PERMISSION)
    constructor(message: String?) : super(HttpErrorType.NO_PERMISSION, message)
    constructor(message: String, detail: Map<String, Any>) : super(HttpErrorType.NO_PERMISSION, message, detail)
}
